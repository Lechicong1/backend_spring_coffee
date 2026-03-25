package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.Response.OrderResp;

import java.util.List;

public interface OrderService {
    OrderResp createOrder(CreateOrderReq request, String staffRole);
    OrderResp createOrderFromCart(CreateOrderFromCartReq request, String staffRole);
    OrderResp updateOrder(Long orderId, UpdateOrderReq request, String staffRole);
    OrderResp getOrderById(Long orderId);
    OrderResp getOrderByCode(String orderCode);
    List<OrderResp> getOrdersByUserId(Long userId);
    List<OrderResp> getAllOrders();
    void deleteOrder(Long orderId, String staffRole);
    void cancelOrder(Long orderId, String staffRole);
}

