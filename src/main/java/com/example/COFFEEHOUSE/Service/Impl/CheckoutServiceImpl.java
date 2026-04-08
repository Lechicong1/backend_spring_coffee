package com.example.COFFEEHOUSE.Service.Impl;


import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.Entity.*;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import com.example.COFFEEHOUSE.Exception.BusinessLogicException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.*;
import com.example.COFFEEHOUSE.Service.CartItemService;
import com.example.COFFEEHOUSE.Service.CheckoutService;
import com.example.COFFEEHOUSE.Service.VoucherService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    @Override
    @Transactional
    public void createOrderFromCart(CreateOrderReq request) {
        Long subtotal = calculateSubtotal(request.getItems());

        voucherService.validateAndUseVoucher(request.getVoucherId(), request.getUserId(), subtotal);
        long voucherDiscount = voucherService.calculateDiscount(request.getVoucherId(), subtotal);

        long totalAmount = Math.max(0, subtotal - voucherDiscount);
        for(OrderItemReq item : request.getItems()) {
            validateStockOrderItem(item);
        }
        OrderEntity savedOrder = saveOrder(request, totalAmount);
        saveOrderItems(savedOrder.getId(), request.getItems());

        cartItemService.clearCart();
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

    private void validateStockOrderItem(OrderItemReq item) {
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
}
