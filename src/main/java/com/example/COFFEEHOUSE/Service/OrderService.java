package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.Response.OrderResp;

import java.util.List;

public interface OrderService {
    OrderResp createOrder(CreateOrderReq request);
    OrderResp createOrderFromCart(CreateOrderFromCartReq request);
    OrderResp updateOrder(Long orderId, UpdateOrderReq request);
    OrderResp getOrderById(Long orderId);
    OrderResp getOrderByCode(String orderCode);
    List<OrderResp> getOrdersByUserId(Long userId);
    List<OrderResp> getAllOrders();
    void deleteOrder(Long orderId);
    void cancelOrder(Long orderId);
}

