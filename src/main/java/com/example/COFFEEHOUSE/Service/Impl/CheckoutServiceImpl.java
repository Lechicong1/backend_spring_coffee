package com.example.COFFEEHOUSE.Service.Impl;


import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.Entity.OrderEntity;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import com.example.COFFEEHOUSE.Reposistory.*;
import com.example.COFFEEHOUSE.Service.CartItemService;
import com.example.COFFEEHOUSE.Service.CheckoutService;
import com.example.COFFEEHOUSE.Service.VoucherService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final VoucherService voucherService;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public void createOrderFromCart(CreateOrderReq request) {
        Long subtotal = calculateSubtotal(request.getItems());
        voucherService.validateAndUseVoucher(request.getVoucherId(), request.getUserId(), subtotal);
        long voucherDiscount = voucherService.calculateDiscount(request.getVoucherId(), subtotal);

        long totalAmount = Math.max(0, subtotal - voucherDiscount);

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
}
