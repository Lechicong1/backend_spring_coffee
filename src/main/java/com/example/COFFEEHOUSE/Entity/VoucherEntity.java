package com.example.COFFEEHOUSE.Entity;

import com.example.COFFEEHOUSE.Enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vouchers")
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer pointCost;
    
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    
    private Double discountValue;
    private Double maxDiscountValue;
    private Double minBillTotal;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Integer quantity;
    
    @Builder.Default
    private Integer usedCount = 0;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
