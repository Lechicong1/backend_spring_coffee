package com.example.COFFEEHOUSE.DTO.Response;

import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResp {
    private String storeName;

    private String orderCode;
    private LocalDateTime createdAt;
    private OrderType orderType;
    private String tableNumber;

    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    private Long subtotal;
    private Long voucherDiscount;
    private String voucherCode;
    private Long shippingFee;
    private Long totalAmount;

    private List<InvoiceItemResp> items;
}
