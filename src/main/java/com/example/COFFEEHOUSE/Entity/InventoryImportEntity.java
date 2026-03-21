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
@Table(name = "inventory_imports")
public class InventoryImportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingredient_id", nullable = false)
    private Long ingredientId;

    @Column(name = "import_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal importQuantity;

    @Column(name = "total_cost", nullable = false)
    private Long totalCost;

    @Column(name = "import_date")
    private LocalDateTime importDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @PrePersist
    protected void onCreate() {
        if (importDate == null) {
            importDate = LocalDateTime.now();
        }
    }
}
