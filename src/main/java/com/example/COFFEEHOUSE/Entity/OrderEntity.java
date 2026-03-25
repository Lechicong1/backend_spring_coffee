package com.example.COFFEEHOUSE.Entity;

import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Enums.OrderType;
import com.example.COFFEEHOUSE.Enums.PaymentMethod;
import com.example.COFFEEHOUSE.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false, length = 50)
    private String orderCode;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "table_number")
    private String tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "total_amount", columnDefinition = "bigint default 0")
    @Builder.Default
    private Long totalAmount = 0L;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @Column(name = "shipping_fee", columnDefinition = "bigint default 0")
    @Builder.Default
    private Long shippingFee = 0L;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
