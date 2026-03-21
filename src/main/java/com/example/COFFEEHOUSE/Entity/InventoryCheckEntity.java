package com.example.COFFEEHOUSE.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_checks")
public class InventoryCheckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient", nullable = false, length = 50)
    private String ingredient;

    @Column(name = "theoryQuantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal theoryQuantity;

    @Column(name = "actualQuantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualQuantity;

    @Column(name = "difference", nullable = false, precision = 10, scale = 2)
    private BigDecimal difference;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @PrePersist
    protected void onCreate() {
        if (checkedAt == null) {
            checkedAt = LocalDateTime.now();
        }
    }
}
