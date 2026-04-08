package com.example.COFFEEHOUSE.DTO.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckoutOrderResp {
    private Long orderId;
    private String orderCode;
    private String paymentMethod;
    private String paymentStatus;
    private Long totalAmount;
    private String paymentUrl;
}
