package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;

public interface CheckoutService {
    void createOrderFromCart(CreateOrderReq request);
}
