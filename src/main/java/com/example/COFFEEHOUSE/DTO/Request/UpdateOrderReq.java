package com.example.COFFEEHOUSE.DTO.Request;

import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderReq {
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String tableNumber;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private Long shippingFee;
    private String note;
}

