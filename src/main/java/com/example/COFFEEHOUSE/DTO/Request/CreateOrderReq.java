package com.example.COFFEEHOUSE.DTO.Request;

import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderReq {
    private Long userId;
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private String tableNumber;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private Long shippingFee = 0L;
    private String note;
    private Long voucherId;
    private List<OrderItemReq> items;
}

