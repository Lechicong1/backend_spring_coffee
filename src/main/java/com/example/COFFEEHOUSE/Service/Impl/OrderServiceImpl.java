package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.OrderMapper;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.Response.OrderItemResp;
import com.example.COFFEEHOUSE.DTO.Response.OrderResp;
import com.example.COFFEEHOUSE.Entity.OrderEntity;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import com.example.COFFEEHOUSE.Entity.VoucherEntity;
import com.example.COFFEEHOUSE.Enums.DiscountType;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import com.example.COFFEEHOUSE.Exception.ForbiddenException;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.OrderItemRepo;
import com.example.COFFEEHOUSE.Reposistory.OrderRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductSizeRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Reposistory.VoucherRepo;
import com.example.COFFEEHOUSE.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductSizeRepo productSizeRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final VoucherRepo voucherRepo;
    private final OrderMapper orderMapper;

    /**
     * Tạo đơn hàng mới
     * - Chỉ nhân viên (STAFF) mới được tạo đơn
     * - Kiểm tra giá từ DB để chống gian lận
     * - Tính toán lại tổng tiền với voucher (nếu có)
     */
    @Override
    public OrderResp createOrder(CreateOrderReq request, String staffRole) {
            // Kiểm tra quyền: chỉ STAFF được tạo đơn
            if (!"STAFF".equals(staffRole) && !"ADMIN".equals(staffRole)) {
                throw new ForbiddenException("Chỉ nhân viên mới có quyền tạo đơn hàng");
            }

        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidInputException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        if (request.getOrderType() == null) {
            throw new InvalidInputException("Loại đơn hàng không được để trống");
        }

        if (request.getPaymentMethod() == null) {
            throw new InvalidInputException("Phương thức thanh toán không được để trống");
        }

        // Validate bàn khi đặt tại quầy
        if (request.getOrderType() == OrderType.AT_COUNTER &&
            (request.getTableNumber() == null || request.getTableNumber().trim().isEmpty())) {
            throw new InvalidInputException("Vui lòng chọn số bàn");
        }

        // Tính tổng tiền và tạo order items
        long subtotal = 0;
        List<OrderItemEntity> orderItems = new java.util.ArrayList<>();

        for (OrderItemReq itemReq : request.getItems()) {
            // Kiểm tra product_size_id tồn tại và lấy giá từ DB
            ProductSizeEntity productSize = productSizeRepo.findById(itemReq.getProductSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy kích thước sản phẩm với ID: " + itemReq.getProductSizeId()));

            // ✅ Kiểm tra giá: so sánh giá từ client và DB
            // Nếu khác > 5% thì cảnh báo (tuỳ chỉnh ngưỡng)
            long dbPrice = productSize.getPrice();
            long clientPrice = itemReq.getPrice();
            double priceDiffPercent = Math.abs((double) (dbPrice - clientPrice) / dbPrice) * 100;

            if (priceDiffPercent > 5) {
                throw new InvalidInputException(
                        String.format("Giá sản phẩm không hợp lệ. Giá DB: %d, Giá client: %d", dbPrice, clientPrice));
            }

            // Sử dụng giá từ DB (nguồn sự thật)
            long priceToUse = dbPrice;
            subtotal += priceToUse * itemReq.getQuantity();

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .productSizeId(itemReq.getProductSizeId())
                    .quantity(itemReq.getQuantity())
                    .priceAtPurchase(priceToUse)
                    .note(itemReq.getNote())
                    .build();

            orderItems.add(orderItem);
        }

        // Tính giảm giá voucher (nếu có)
        long voucherDiscount = 0;
        if (request.getVoucherId() != null && request.getVoucherId() > 0) {
            VoucherEntity voucher = voucherRepo.findById(request.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy voucher với ID: " + request.getVoucherId()));

            // Kiểm tra voucher có hợp lệ không
            if (!voucher.getIsActive()) {
                throw new InvalidInputException("Voucher không còn hoạt động");
            }

            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                throw new InvalidInputException("Voucher chưa có hiệu lực");
            }

            if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                throw new InvalidInputException("Voucher đã hết hạn");
            }

            if (voucher.getQuantity() != null && voucher.getUsedCount() >= voucher.getQuantity()) {
                throw new InvalidInputException("Voucher đã hết số lượng");
            }

            // Kiểm tra giá tối thiểu
            if (voucher.getMinBillTotal() != null && subtotal < voucher.getMinBillTotal()) {
                throw new InvalidInputException(
                        String.format("Tổng tiền phải >= %d để dùng voucher này", voucher.getMinBillTotal().longValue()));
            }

            // Tính giảm giá
            if (DiscountType.FIXED == voucher.getDiscountType()) {
                voucherDiscount = voucher.getDiscountValue().longValue();
            } else {
                // PERCENT
                voucherDiscount = Math.round(subtotal * (voucher.getDiscountValue() / 100.0));
            }

            // Áp dụng giảm giá tối đa
            if (voucher.getMaxDiscountValue() != null && voucherDiscount > voucher.getMaxDiscountValue()) {
                voucherDiscount = voucher.getMaxDiscountValue().longValue();
            }

            // Giảm giá không quá tổng tiền
            voucherDiscount = Math.min(voucherDiscount, subtotal);
        }

        long totalAmount = Math.max(0, subtotal - voucherDiscount);

        // Tạo Order entity
        String orderCode = generateOrderCode();
        OrderEntity order = OrderEntity.builder()
                .orderCode(orderCode)
                .userId(request.getUserId())
                .orderType(request.getOrderType())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(request.getPaymentMethod())
                .tableNumber(request.getTableNumber())
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingFee(request.getShippingFee() != null ? request.getShippingFee() : 0L)
                .note(request.getNote())
                .voucherId(request.getVoucherId())
                .build();

        OrderEntity savedOrder = orderRepo.save(order);

        // Lưu order items
        for (OrderItemEntity item : orderItems) {
            item.setOrderId(savedOrder.getId());
            orderItemRepo.save(item);
        }

        return mapOrderToResp(savedOrder, orderItems);
    }

    /**
     * Tạo đơn hàng từ cart (CreateOrderFromCartReq)
     * - Được sử dụng bởi hệ thống POS
     * - Kiểm tra giá từ DB để chống gian lận
     */
    @Override
    public OrderResp createOrderFromCart(CreateOrderFromCartReq request, String staffRole) {
        // Kiểm tra quyền: chỉ STAFF được tạo đơn
        if (!"STAFF".equals(staffRole) && !"ADMIN".equals(staffRole)) {
            throw new ForbiddenException("Chỉ nhân viên mới có quyền tạo đơn hàng");
        }

        // Validate request
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new InvalidInputException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        if (request.getOrderType() == null || request.getOrderType().trim().isEmpty()) {
            throw new InvalidInputException("Loại đơn hàng không được để trống");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            throw new InvalidInputException("Phương thức thanh toán không được để trống");
        }

        // Parse OrderType từ string
        OrderType orderType;
        try {
            orderType = OrderType.valueOf(request.getOrderType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Loại đơn hàng không hợp lệ: " + request.getOrderType());
        }

        // Parse PaymentMethod từ string
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Phương thức thanh toán không hợp lệ: " + request.getPaymentMethod());
        }

        // Validate bàn khi đặt tại quầy
        if (orderType == OrderType.AT_COUNTER &&
            (request.getTableNumber() == null || request.getTableNumber().trim().isEmpty())) {
            throw new InvalidInputException("Vui lòng chọn số bàn");
        }

        // Tính tổng tiền và tạo order items
        long subtotal = 0;
        List<OrderItemEntity> orderItems = new java.util.ArrayList<>();

        for (OrderItemReq itemReq : request.getCartItems()) {
            // Kiểm tra product_size_id tồn tại và lấy giá từ DB
            ProductSizeEntity productSize = productSizeRepo.findById(itemReq.getProductSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy kích thước sản phẩm với ID: " + itemReq.getProductSizeId()));

            // ✅ Kiểm tra giá: so sánh giá từ client và DB
            long dbPrice = productSize.getPrice();
            long clientPrice = itemReq.getPrice();
            double priceDiffPercent = Math.abs((double) (dbPrice - clientPrice) / dbPrice) * 100;

            if (priceDiffPercent > 5) {
                throw new InvalidInputException(
                        String.format("Giá sản phẩm không hợp lệ. Giá DB: %d, Giá client: %d", dbPrice, clientPrice));
            }

            // Sử dụng giá từ DB (nguồn sự thật)
            long priceToUse = dbPrice;
            subtotal += priceToUse * itemReq.getQuantity();

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .productSizeId(itemReq.getProductSizeId())
                    .quantity(itemReq.getQuantity())
                    .priceAtPurchase(priceToUse)
                    .note(itemReq.getNote())
                    .build();

            orderItems.add(orderItem);
        }

        // Tính giảm giá voucher (nếu có)
        long voucherDiscount = 0;
        if (request.getVoucherId() != null && request.getVoucherId() > 0) {
            VoucherEntity voucher = voucherRepo.findById(request.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy voucher với ID: " + request.getVoucherId()));

            // Kiểm tra voucher có hợp lệ không
            if (!voucher.getIsActive()) {
                throw new InvalidInputException("Voucher không còn hoạt động");
            }

            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                throw new InvalidInputException("Voucher chưa có hiệu lực");
            }

            if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                throw new InvalidInputException("Voucher đã hết hạn");
            }

            if (voucher.getQuantity() != null && voucher.getUsedCount() >= voucher.getQuantity()) {
                throw new InvalidInputException("Voucher đã hết số lượng");
            }

            // Kiểm tra giá tối thiểu
            if (voucher.getMinBillTotal() != null && subtotal < voucher.getMinBillTotal()) {
                throw new InvalidInputException(
                        String.format("Tổng tiền phải >= %d để dùng voucher này", voucher.getMinBillTotal().longValue()));
            }

            // Tính giảm giá
            if (DiscountType.FIXED == voucher.getDiscountType()) {
                voucherDiscount = voucher.getDiscountValue().longValue();
            } else {
                // PERCENT
                voucherDiscount = Math.round(subtotal * (voucher.getDiscountValue() / 100.0));
            }

            // Áp dụng giảm giá tối đa
            if (voucher.getMaxDiscountValue() != null && voucherDiscount > voucher.getMaxDiscountValue()) {
                voucherDiscount = voucher.getMaxDiscountValue().longValue();
            }

            // Giảm giá không quá tổng tiền
            voucherDiscount = Math.min(voucherDiscount, subtotal);
        }

        long totalAmount = Math.max(0, subtotal - voucherDiscount);

        // Tạo Order entity
        String orderCode = generateOrderCode();
        OrderEntity order = OrderEntity.builder()
                .orderCode(orderCode)
                .userId(request.getUserId())
                .orderType(orderType)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(paymentMethod)
                .tableNumber(request.getTableNumber())
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingFee(request.getShippingFee() != null ? request.getShippingFee() : 0L)
                .note(request.getNote())
                .voucherId(request.getVoucherId())
                .build();

        OrderEntity savedOrder = orderRepo.save(order);

        // Lưu order items
        for (OrderItemEntity item : orderItems) {
            item.setOrderId(savedOrder.getId());
            orderItemRepo.save(item);
        }

        return mapOrderToResp(savedOrder, orderItems);
    }

    /**
     * Cập nhật đơn hàng
     * - Chỉ STAFF/ADMIN được cập nhật
     * - Không được sửa giá (nhưng có thể sửa trạng thái, bàn, v.v.)
     */
    @Override
    public OrderResp updateOrder(Long orderId, UpdateOrderReq request, String staffRole) {
        if (!"STAFF".equals(staffRole) && !"ADMIN".equals(staffRole)) {
            throw new ForbiddenException("Chỉ nhân viên mới có quyền cập nhật đơn hàng");
        }

        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Cập nhật các trường được phép
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        if (request.getPaymentMethod() != null) {
            order.setPaymentMethod(request.getPaymentMethod());
        }

        if (request.getTableNumber() != null) {
            order.setTableNumber(request.getTableNumber());
        }

        if (request.getShippingAddress() != null) {
            order.setShippingAddress(request.getShippingAddress());
        }

        if (request.getReceiverName() != null) {
            order.setReceiverName(request.getReceiverName());
        }

        if (request.getReceiverPhone() != null) {
            order.setReceiverPhone(request.getReceiverPhone());
        }

        if (request.getShippingFee() != null) {
            order.setShippingFee(request.getShippingFee());
        }

        if (request.getNote() != null) {
            order.setNote(request.getNote());
        }

        OrderEntity updatedOrder = orderRepo.save(order);
        List<OrderItemEntity> items = orderItemRepo.findByOrderId(orderId);

        return mapOrderToResp(updatedOrder, items);
    }

    /**
     * Lấy đơn hàng theo ID
     */
    @Override
    public OrderResp getOrderById(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        List<OrderItemEntity> items = orderItemRepo.findByOrderId(orderId);
        return mapOrderToResp(order, items);
    }

    /**
     * Lấy đơn hàng theo order code
     */
    @Override
    public OrderResp getOrderByCode(String orderCode) {
        OrderEntity order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với code: " + orderCode));

        List<OrderItemEntity> items = orderItemRepo.findByOrderId(order.getId());
        return mapOrderToResp(order, items);
    }

    /**
     * Lấy danh sách đơn hàng của 1 khách
     */
    @Override
    public List<OrderResp> getOrdersByUserId(Long userId) {
        List<OrderEntity> orders = orderRepo.findByUserId(userId);

        return orders.stream()
                .map(order -> {
                    List<OrderItemEntity> items = orderItemRepo.findByOrderId(order.getId());
                    return mapOrderToResp(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả đơn hàng
     */
    @Override
    public List<OrderResp> getAllOrders() {
        List<OrderEntity> orders = orderRepo.findAll();

        return orders.stream()
                .map(order -> {
                    List<OrderItemEntity> items = orderItemRepo.findByOrderId(order.getId());
                    return mapOrderToResp(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Xóa đơn hàng
     * - Chỉ ADMIN được xóa
     * - Chỉ xóa đơn PENDING
     */
    @Override
    public void deleteOrder(Long orderId, String staffRole) {
        if (!"ADMIN".equals(staffRole)) {
            throw new ForbiddenException("Chỉ ADMIN mới có quyền xóa đơn hàng");
        }

        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidInputException("Chỉ được xóa đơn hàng có trạng thái PENDING");
        }

        // Xóa order items trước
        List<OrderItemEntity> items = orderItemRepo.findByOrderId(orderId);
        orderItemRepo.deleteAll(items);

        // Xóa order
        orderRepo.deleteById(orderId);
    }

    /**
     * Hủy đơn hàng
     * - Có thể hủy từ các trạng thái nhất định
     */
    @Override
    public void cancelOrder(Long orderId, String staffRole) {
        if (!"STAFF".equals(staffRole) && !"ADMIN".equals(staffRole)) {
            throw new ForbiddenException("Chỉ nhân viên mới có quyền hủy đơn hàng");
        }

        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidInputException("Không thể hủy đơn hàng đã hoàn thành hoặc đã hủy");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    /**
     * Helper: Tạo order code
     */
    private String generateOrderCode() {
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);
        return "ORD" + randomNum;
    }

    /**
     * Helper: Map OrderEntity -> OrderResp
     */
    private OrderResp mapOrderToResp(OrderEntity order, List<OrderItemEntity> items) {
        OrderResp resp = orderMapper.toResponse(order);

        // ✅ Lấy tên khách hàng
        if (order.getUserId() != null) {
            String customerName = userRepo.findById(order.getUserId())
                    .map(user -> user.getFullName() != null ? user.getFullName() : user.getUsername())
                    .orElse("Khách hàng");
            resp.setCustomerName(customerName);
        } else {
            resp.setCustomerName("Khách vãng lai");
        }

        List<OrderItemResp> itemResponses = items.stream()
                .map(item -> {
                    // ✅ JOIN với product_sizes và products để lấy tên
                    ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId())
                            .orElse(null);
                    
                    String productName = "Unknown Product";
                    String sizeName = "Unknown Size";
                    
                    if (productSize != null) {
                        sizeName = productSize.getSizeName();
                        productName = productRepo.findById(productSize.getProductId())
                                .map(p -> p.getName())
                                .orElse("Unknown Product");
                    }
                    
                    return OrderItemResp.builder()
                            .id(item.getId())
                            .productSizeId(item.getProductSizeId())
                            .quantity(item.getQuantity())
                            .priceAtPurchase(item.getPriceAtPurchase())
                            .note(item.getNote())
                            .productName(productName)
                            .sizeName(sizeName)
                            .build();
                })
                .collect(Collectors.toList());

        resp.setItems(itemResponses);
        return resp;
    }
}
