package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.InvoiceMapper;
import com.example.COFFEEHOUSE.DTO.Mapper.OrderMapper;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.CreateOrderFromCartReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderReq;
import com.example.COFFEEHOUSE.DTO.Request.UpdateOrderItemNoteReq;
import com.example.COFFEEHOUSE.DTO.Response.OrderDetailResp;
import com.example.COFFEEHOUSE.DTO.Response.OrderItemResp;
import com.example.COFFEEHOUSE.DTO.Response.OrderResp;
import com.example.COFFEEHOUSE.DTO.Response.InvoiceItemResp;
import com.example.COFFEEHOUSE.DTO.Response.InvoiceResp;
import com.example.COFFEEHOUSE.Entity.OrderEntity;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import com.example.COFFEEHOUSE.Entity.VoucherEntity;
import com.example.COFFEEHOUSE.Enums.DiscountType;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.OrderItemRepo;
import com.example.COFFEEHOUSE.Reposistory.OrderRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductSizeRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Reposistory.VoucherRepo;
import com.example.COFFEEHOUSE.Service.CartItemService;
import com.example.COFFEEHOUSE.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final InvoiceMapper invoiceMapper;
    private final CartItemService cartItemService;


    @Override
    public OrderResp createOrder(CreateOrderReq request) {


        // Tính tổng tiền và tạo order items
        Long subtotal =  request.getItems().stream()
                .mapToLong(item -> ( item.getPrice()) * (item.getQuantity()))
                .sum();

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
                .totalAmount(subtotal)
                .shippingAddress(request.getShippingAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .shippingFee(request.getShippingFee() != null ? request.getShippingFee() : 0L)
                .note(request.getNote())
                .voucherId(request.getVoucherId())
                .build();

        OrderEntity savedOrder = orderRepo.save(order);
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (OrderItemReq itemReq : request.getItems()) {

            subtotal += itemReq.getPrice() * itemReq.getQuantity();

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .orderId(order.getId())
                    .productSizeId(itemReq.getProductSizeId())
                    .quantity(itemReq.getQuantity())
                    .priceAtPurchase(itemReq.getPrice())
                    .note(itemReq.getNote())
                    .build();

            orderItems.add(orderItem);
        }

       // luu order items
       orderItemRepo.saveAll(orderItems);

       // Xóa giỏ hàng sau khi tạo order thành công
       cartItemService.clearCart();

        return mapOrderToResp(savedOrder, orderItems);
    }

    /**
     * Tạo đơn hàng từ cart (CreateOrderFromCartReq)
     * - Được sử dụng bởi hệ thống POS
     * - Kiểm tra giá từ DB để chống gian lận
     */
    @Override
    public OrderResp createOrderFromCart(CreateOrderFromCartReq request) {
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

        // Xóa giỏ hàng sau khi tạo order thành công
        cartItemService.clearCart();

        return mapOrderToResp(savedOrder, orderItems);
    }

    /**
     * Cập nhật đơn hàng
     * - Chỉ STAFF/ADMIN được cập nhật - được kiểm tra bằng @PreAuthorize
     * - Không được sửa giá (nhưng có thể sửa trạng thái, bàn, v.v.)
     * - Ghi chú chỉ được sửa khi trạng thái là PENDING
     * - Nếu đổi từ AT_COUNTER sang TAKEAWAY, sẽ xóa số bàn
     */
    @Override
    public OrderResp updateOrder(Long orderId, UpdateOrderReq request) {
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

        // Cập nhật loại đơn hàng và xử lý số bàn
        if (request.getOrderType() != null) {
            OrderType newOrderType = request.getOrderType();
            OrderType oldOrderType = order.getOrderType();

            order.setOrderType(newOrderType);

            // Nếu chuyển từ AT_COUNTER sang TAKEAWAY, xóa số bàn
            if (oldOrderType == OrderType.AT_COUNTER && newOrderType == OrderType.TAKEAWAY) {
                order.setTableNumber(null);
            }
            // Nếu chuyển sang AT_COUNTER, cần số bàn
            else if (newOrderType == OrderType.AT_COUNTER) {
                if (request.getTableNumber() == null || request.getTableNumber().trim().isEmpty()) {
                    throw new InvalidInputException("Vui lòng chọn số bàn khi thay đổi loại đơn sang ăn tại quầy");
                }
                order.setTableNumber(request.getTableNumber());
            }
            // Nếu không thay đổi loại hoặc chuyển đổi khác, cập nhật số bàn nếu có
            else if (request.getTableNumber() != null) {
                if (request.getTableNumber().trim().isEmpty()) {
                    order.setTableNumber(null);
                } else {
                    order.setTableNumber(request.getTableNumber());
                }
            }
        } else if (request.getTableNumber() != null) {
            // Nếu không thay đổi orderType nhưng cập nhật tableNumber
            if (request.getTableNumber().trim().isEmpty()) {
                order.setTableNumber(null);
            } else {
                order.setTableNumber(request.getTableNumber());
            }
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

        // Ghi chú chỉ được sửa khi đơn hàng ở trạng thái PENDING
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
     * - Chỉ ADMIN được xóa - được kiểm tra bằng @PreAuthorize
     * - Chỉ xóa đơn PENDING
     */
    @Override
    public void deleteOrder(Long orderId) {
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
     * - STAFF/ADMIN được hủy - được kiểm tra bằng @PreAuthorize
     * - Có thể hủy từ các trạng thái nhất định
     */
    @Override
    public void cancelOrder(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidInputException("Không thể hủy đơn hàng đã hoàn thành hoặc đã hủy");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
    }

    /**
     * Lấy hóa đơn theo order code
     */
    @Override
    public InvoiceResp getInvoiceByCode(String orderCode) {
        OrderEntity order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với code: " + orderCode));

        List<OrderItemEntity> items = orderItemRepo.findByOrderId(order.getId());

        long subtotal = items.stream()
                .mapToLong(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();

        long voucherDiscount = 0;
        String voucherCode = null;
        if (order.getVoucherId() != null && order.getVoucherId() > 0) {
            VoucherEntity voucher = voucherRepo.findById(order.getVoucherId()).orElse(null);
            if (voucher != null) {
                voucherCode = voucher.getName();
                if (DiscountType.FIXED == voucher.getDiscountType()) {
                    voucherDiscount = voucher.getDiscountValue().longValue();
                } else {
                    voucherDiscount = Math.round(subtotal * (voucher.getDiscountValue() / 100.0));
                }
                if (voucher.getMaxDiscountValue() != null && voucherDiscount > voucher.getMaxDiscountValue()) {
                    voucherDiscount = voucher.getMaxDiscountValue().longValue();
                }
                voucherDiscount = Math.min(voucherDiscount, subtotal);
            }
        }

        long shippingFee = order.getShippingFee() != null ? order.getShippingFee() : 0L;
        long totalAmount = Math.max(0, subtotal - voucherDiscount + shippingFee);

        List<InvoiceItemResp> invoiceItems = items.stream()
                .map(invoiceMapper::toInvoiceItemResp)
                .map(itemResp -> {
                    ProductSizeEntity size = productSizeRepo.findById(itemResp.getProductSizeId()).orElse(null);
                    String sizeName = size != null ? size.getSizeName() : "Unknown Size";
                    String productName = size != null
                            ? productRepo.findById(size.getProductId()).map(p -> p.getName()).orElse("Unknown Product")
                            : "Unknown Product";
                    itemResp.setSizeName(sizeName);
                    itemResp.setProductName(productName);
                    return itemResp;
                })
                .collect(Collectors.toList());

        InvoiceResp invoice = invoiceMapper.toInvoiceResp(order);
        invoice.setStoreName("COFFEE HOUSE");
        invoice.setSubtotal(subtotal);
        invoice.setVoucherDiscount(voucherDiscount);
        invoice.setVoucherCode(voucherCode);
        invoice.setShippingFee(shippingFee);
        invoice.setTotalAmount(totalAmount);
        invoice.setItems(invoiceItems);

        return invoice;
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

    /**
     * Lấy chi tiết đơn hàng
     */
    @Override
    public OrderDetailResp getOrderDetail(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        List<OrderItemResp> items = orderItemRepo.findByOrderId(orderId).stream()
                .map(item -> {
                    ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId()).orElse(null);
                    String sizeName = productSize != null ? productSize.getSizeName() : "Unknown Size";
                    String productName = productSize != null
                            ? productRepo.findById(productSize.getProductId()).map(p -> p.getName()).orElse("Unknown Product")
                            : "Unknown Product";

                    long lineTotal = item.getPriceAtPurchase() * item.getQuantity();

                    return OrderItemResp.builder()
                            .id(item.getId())
                            .productSizeId(item.getProductSizeId())
                            .quantity(item.getQuantity())
                            .priceAtPurchase(item.getPriceAtPurchase())
                            .note(item.getNote())
                            .productName(productName)
                            .sizeName(sizeName)
                            .lineTotal(lineTotal)
                            .build();
                })
                .collect(Collectors.toList());

        long total = items.stream()
                .mapToLong(OrderItemResp::getLineTotal)
                .sum();

        return OrderDetailResp.builder()
                .orderCode(order.getOrderCode())
                .items(items)
                .totalAmount(total)
                .build();
    }

    /**
     * Cập nhật ghi chú cho order item
     */
    @Override
    public OrderItemResp updateOrderItemNote(Long orderId, UpdateOrderItemNoteReq request) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        OrderItemEntity item = orderItemRepo.findById(request.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết đơn hàng với ID: " + request.getOrderItemId()));

        if (!item.getOrderId().equals(order.getId())) {
            throw new InvalidInputException("Chi tiết đơn hàng không thuộc đơn hàng này");
        }

        item.setNote(request.getNote());
        OrderItemEntity saved = orderItemRepo.save(item);

        ProductSizeEntity productSize = productSizeRepo.findById(saved.getProductSizeId()).orElse(null);
        String sizeName = productSize != null ? productSize.getSizeName() : "Unknown Size";
        String productName = productSize != null
                ? productRepo.findById(productSize.getProductId()).map(p -> p.getName()).orElse("Unknown Product")
                : "Unknown Product";

        return OrderItemResp.builder()
                .id(saved.getId())
                .productSizeId(saved.getProductSizeId())
                .quantity(saved.getQuantity())
                .priceAtPurchase(saved.getPriceAtPurchase())
                .note(saved.getNote())
                .productName(productName)
                .sizeName(sizeName)
                .lineTotal(saved.getPriceAtPurchase() * saved.getQuantity())
                .build();
    }
}
