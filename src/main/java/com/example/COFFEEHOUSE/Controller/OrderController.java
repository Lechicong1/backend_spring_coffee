package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Reusable response messages (reduce duplicated literals)
    private static final String MSG_CREATED = "Đơn hàng được tạo thành công";
    private static final String MSG_UPDATED = "Đơn hàng được cập nhật thành công";
    private static final String MSG_DETAIL = "Lấy chi tiết đơn hàng thành công";
    private static final String MSG_LIST = "Lấy danh sách đơn hàng thành công";
    private static final String MSG_DELETED = "Đơn hàng được xóa thành công";
    private static final String MSG_CANCELLED = "Đơn hàng được hủy thành công";

    /**
     * POST /orders - Tạo đơn hàng mới
     * Chỉ STAFF/ADMIN mới được tạo
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResponseData> createOrder(
            @RequestBody CreateOrderReq request) {

        var orderResp = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message(MSG_CREATED)
                        .data(orderResp)
                        .build());
    }

    /**
     * POST /orders/from-cart - Tạo đơn hàng từ cart (dùng cho POS)
     * Chỉ STAFF/ADMIN mới được tạo
     */
    @PostMapping("/from-cart")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResponseData> createOrderFromCart(
            @RequestBody CreateOrderFromCartReq request) {

        var orderResp = orderService.createOrderFromCart(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message(MSG_CREATED)
                        .data(orderResp)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SHIPPER')")
    public ResponseEntity<ResponseData> updateOrder(
            @PathVariable Long id,
            @RequestBody UpdateOrderReq request) {

        var orderResp = orderService.updateOrder(id, request);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_UPDATED)
                .data(orderResp)
                .build());
    }

    /**
     * GET /orders/{id} - Lấy chi tiết đơn hàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getOrderById(@PathVariable Long id) {
        var orderResp = orderService.getOrderById(id);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_DETAIL)
                .data(orderResp)
                .build());
    }

    /**
     * GET /orders/code/{code} - Lấy đơn hàng theo order code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseData> getOrderByCode(@PathVariable String code) {
        var orderResp = orderService.getOrderByCode(code);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_DETAIL)
                .data(orderResp)
                .build());
    }

    /**
     * GET /orders/user/{userId} - Lấy danh sách đơn hàng của khách
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData> getOrdersByUserId(@PathVariable Long userId) {
        var orders = orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_LIST)
                .data(orders)
                .build());
    }

    /**
     * GET /orders - Lấy tất cả đơn hàng
     * Chỉ STAFF/ADMIN được xem
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResponseData> getAllOrders() {
        var orders = orderService.getAllOrders();

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_LIST)
                .data(orders)
                .build());
    }

    /**
     * DELETE /orders/{id} - Xóa đơn hàng
     * Chỉ ADMIN được xóa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_DELETED)
                .build());
    }

    /**
     * PUT /orders/{id}/cancel - Hủy đơn hàng
     * STAFF/ADMIN được hủy
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ResponseData> cancelOrder(
            @PathVariable Long id) {

        orderService.cancelOrder(id);

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(MSG_CANCELLED)
                .build());
    }
}
