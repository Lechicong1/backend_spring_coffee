package com.example.COFFEEHOUSE.DTO.Request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderFromCartReq {
    @JsonProperty("user_id")
    @JsonAlias("userId")
    private Long userId;

    @JsonProperty("order_type")
    @JsonAlias("orderType")
    private String orderType;

    @JsonProperty("payment_method")
    @JsonAlias("paymentMethod")
    private String paymentMethod;

    @JsonProperty("table_number")
    @JsonAlias("tableNumber")
    private String tableNumber;

    @JsonProperty("shipping_address")
    @JsonAlias("shippingAddress")
    private String shippingAddress;

    @JsonProperty("receiver_name")
    @JsonAlias("receiverName")
    private String receiverName;

    @JsonProperty("receiver_phone")
    @JsonAlias("receiverPhone")
    private String receiverPhone;

    @JsonProperty("shipping_fee")
    @JsonAlias("shippingFee")
    private Long shippingFee = 0L;

    private String note;

    @JsonProperty("voucher_id")
    @JsonAlias("voucherId")
    private Long voucherId;

    @JsonProperty("cart_items")
    @JsonAlias("cartItems")
    private List<OrderItemReq> cartItems;
}
