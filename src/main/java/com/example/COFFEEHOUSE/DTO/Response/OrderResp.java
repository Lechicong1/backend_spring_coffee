package com.example.COFFEEHOUSE.DTO.Response;

import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResp {
    private Long id;
    private String orderCode;
    private Long userId;
    private String customerName;
    private OrderType orderType;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String tableNumber;
    private PaymentMethod paymentMethod;
    private Long totalAmount;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private Long shippingFee;
    private String note;
    private Long voucherId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResp> items;
}
