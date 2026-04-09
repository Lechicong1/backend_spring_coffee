package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Response.CheckoutOrderResp;
import com.example.COFFEEHOUSE.DTO.Response.VnpayReturnResp;
import com.example.COFFEEHOUSE.Entity.*;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import com.example.COFFEEHOUSE.Exception.BusinessLogicException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.*;
import com.example.COFFEEHOUSE.Config.VnpayProperties;
import com.example.COFFEEHOUSE.Service.CartItemService;
import com.example.COFFEEHOUSE.Service.CheckoutService;
import com.example.COFFEEHOUSE.Service.VoucherService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import com.example.COFFEEHOUSE.Utils.VnpayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final VoucherService voucherService;
    private final CartItemService cartItemService;
    private final RecipeRepo recipesRepo;
    private final ProductSizeRepo productSizeRepo;
    private final IngredientRepo ingredientRepo;
    private final VnpayProperties vnpayProperties;

    @Override
    @Transactional
    public CheckoutOrderResp createOrderFromCart(CreateOrderReq request, String clientIp) {
        Long subtotal = calculateSubtotal(request.getItems());

        voucherService.validateAndUseVoucher(request.getVoucherId(), request.getUserId(), subtotal);
        long voucherDiscount = voucherService.calculateDiscount(request.getVoucherId(), subtotal);

        long totalAmount = Math.max(0, subtotal - voucherDiscount);
        for (OrderItemReq item : request.getItems()) {
            validateStockOrderItem(item);
        }

        deductIngredients(request.getItems());

        OrderEntity savedOrder = saveOrder(request, totalAmount);
        saveOrderItems(savedOrder.getId(), request.getItems());

        String paymentUrl = null;
        if (isVnpayPayment(request.getPaymentMethod())) {
            paymentUrl = buildVnpayPaymentUrl(savedOrder, clientIp);
        }

        cartItemService.clearCart();

        return CheckoutOrderResp.builder()
                .orderId(savedOrder.getId())
                .orderCode(savedOrder.getOrderCode())
                .paymentMethod(savedOrder.getPaymentMethod() != null ? savedOrder.getPaymentMethod().name() : null)
                .paymentStatus(savedOrder.getPaymentStatus() != null ? savedOrder.getPaymentStatus().name() : null)
                .totalAmount(savedOrder.getTotalAmount())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public VnpayReturnResp handleVnpayReturn(Map<String, String> vnpayParams) {
        Map<String, String> params = new HashMap<>(vnpayParams);
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String signedData = VnpayUtils.buildQueryData(params);
        String expectedHash = VnpayUtils.hmacSha512(vnpayProperties.getHashSecret(), signedData);
        boolean validSignature = receivedHash != null && receivedHash.equalsIgnoreCase(expectedHash);

        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String transactionStatus = params.get("vnp_TransactionStatus");

        if (!validSignature) {
            return VnpayReturnResp.builder()
                    .success(false)
                    .orderCode(orderCode)
                    .message("Sai chữ ký thanh toán VNPAY")
                    .transactionNo(transactionNo)
                    .responseCode(responseCode)
                    .build();
        }

        if (orderCode == null || orderCode.isBlank()) {
            return VnpayReturnResp.builder()
                    .success(false)
                    .orderCode(null)
                    .message("Thiếu mã đơn hàng từ VNPAY")
                    .transactionNo(transactionNo)
                    .responseCode(responseCode)
                    .build();
        }

        OrderEntity order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));

        boolean paid = "00".equals(responseCode) && (transactionStatus == null || "00".equals(transactionStatus));
        if (paid && order.getPaymentStatus() != PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.PAID);
            orderRepo.save(order);
        }

        return VnpayReturnResp.builder()
                .success(paid)
                .orderCode(orderCode)
                .message(paid ? "Thanh toán thành công" : "Thanh toán thất bại hoặc bị hủy")
                .transactionNo(transactionNo)
                .responseCode(responseCode)
                .build();
    }

    private Long calculateSubtotal(List<OrderItemReq> items) {
        return items.stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private OrderEntity saveOrder(CreateOrderReq request, long totalAmount) {
        OrderEntity order = OrderEntity.builder()
                .orderCode(CommonUtils.generateOrderCode())
                .userId(request.getUserId())
                .orderType(request.getOrderType())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(request.getPaymentMethod())
                .tableNumber(request.getTableNumber())
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingFee(request.getShippingFee() != null ? request.getShippingFee() : 0L)
                .note(request.getNote())
                .voucherId(request.getVoucherId())
                .build();
        return orderRepo.save(order);
    }

    private void saveOrderItems(Long orderId, List<OrderItemReq> items) {
        List<OrderItemEntity> orderItems = items.stream()
                .map(itemReq -> OrderItemEntity.builder()
                        .orderId(orderId)
                        .productSizeId(itemReq.getProductSizeId())
                        .quantity(itemReq.getQuantity())
                        .priceAtPurchase(itemReq.getPrice())
                        .note(itemReq.getNote())
                        .build())
                .collect(Collectors.toList());
        orderItemRepo.saveAll(orderItems);
    }

    public void validateStockOrderItem(OrderItemReq item) {
        float multiplier = CommonUtils.getMultiplierBySize(item.getSizeName());
        ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm size này"));
        List<RecipeEntity> recipes = recipesRepo.findByProductId(productSize.getProductId());
        if (recipes == null || recipes.isEmpty()) {
            throw new BusinessLogicException("Không tìm thấy công thức cho sản phẩm size này");
        }

        List<Long> ingredientIds = recipes.stream().map(RecipeEntity::getIngredientId).collect(Collectors.toList());
        List<IngredientEntity> ingredients = ingredientRepo.findAllById(ingredientIds);
        if (ingredients == null || ingredients.size() != ingredientIds.size()) {
            throw new BusinessLogicException("Không tìm thấy đủ nguyên liệu cho công thức");
        }

        Map<Long, IngredientEntity> ingredientMap = ingredients.stream()
                .collect(Collectors.toMap(IngredientEntity::getId, ing -> ing));

        for (RecipeEntity recipe : recipes) {
            IngredientEntity ingredient = ingredientMap.get(recipe.getIngredientId());
            double requiredAmount = recipe.getBaseAmount() * multiplier * item.getQuantity();

            if (ingredient.getStockQuantity() < requiredAmount) {
                throw new BusinessLogicException("Nguyên liệu " + ingredient.getName() + " không đủ để chế biến sản phẩm");
            }
        }
    }

    public void deductIngredients(List<OrderItemReq> items) {
        for (OrderItemReq item : items) {
            float multiplier = CommonUtils.getMultiplierBySize(item.getSizeName());
            ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm size này"));

            List<RecipeEntity> recipes = recipesRepo.findByProductId(productSize.getProductId());
            List<Long> ingredientIds = recipes.stream()
                    .map(RecipeEntity::getIngredientId)
                    .collect(Collectors.toList());

            List<IngredientEntity> ingredients = ingredientRepo.findAllById(ingredientIds);
            Map<Long, IngredientEntity> ingredientMap = ingredients.stream()
                    .collect(Collectors.toMap(IngredientEntity::getId, ing -> ing));

            for (RecipeEntity recipe : recipes) {
                IngredientEntity ingredient = ingredientMap.get(recipe.getIngredientId());
                double requiredAmount = recipe.getBaseAmount() * multiplier * item.getQuantity();
                ingredient.setStockQuantity(ingredient.getStockQuantity() - requiredAmount);
            }

            ingredientRepo.saveAll(ingredients);
        }
    }

    private boolean isVnpayPayment(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.BANKING || paymentMethod == PaymentMethod.CARD;
    }

    private String buildVnpayPaymentUrl(OrderEntity order, String clientIp) {
        long amountToPay = (order.getTotalAmount() != null ? order.getTotalAmount() : 0L)
                + (order.getShippingFee() != null ? order.getShippingFee() : 0L);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amountToPay * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", order.getOrderCode());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderCode());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());
        vnpParams.put("vnp_IpAddr", (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp);
        vnpParams.put("vnp_CreateDate", now.format(formatter));
        vnpParams.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

        String queryData = VnpayUtils.buildQueryData(vnpParams);
        String secureHash = VnpayUtils.hmacSha512(vnpayProperties.getHashSecret(), queryData);
        return vnpayProperties.getPayUrl() + "?" + queryData + "&vnp_SecureHash=" + secureHash;
    }
}
