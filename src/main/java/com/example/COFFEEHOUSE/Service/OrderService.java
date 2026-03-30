package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderItemNoteReq;
import com.example.COFFEEHOUSE.DTO.Response.OrderResp;
import com.example.COFFEEHOUSE.DTO.Response.InvoiceResp;
import com.example.COFFEEHOUSE.DTO.Response.OrderDetailResp;
import com.example.COFFEEHOUSE.DTO.Response.OrderItemResp;

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
    InvoiceResp getInvoiceByCode(String orderCode);
    OrderDetailResp getOrderDetail(Long orderId);
    OrderItemResp updateOrderItemNote(Long orderId, UpdateOrderItemNoteReq request);
}
