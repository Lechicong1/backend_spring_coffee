package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Response.CheckoutOrderResp;
import com.example.COFFEEHOUSE.DTO.Response.VnpayReturnResp;

import java.util.Map;

public interface CheckoutService {
    CheckoutOrderResp createOrderFromCart(CreateOrderReq request, String clientIp);
    VnpayReturnResp handleVnpayReturn(Map<String, String> vnpayParams);
    void validateStockOrderItem(OrderItemReq item);
}
