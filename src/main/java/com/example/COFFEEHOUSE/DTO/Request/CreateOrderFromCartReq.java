package com.example.COFFEEHOUSE.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderFromCartReq {
    private Long userId;
    private String orderType;
    private String paymentMethod;
    private String tableNumber;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private Long shippingFee = 0L;
    private String note;

    @JsonProperty("voucher_id")
    private Long voucherId;

    @JsonProperty("cart_items")
    private List<OrderItemReq> cartItems;
}

