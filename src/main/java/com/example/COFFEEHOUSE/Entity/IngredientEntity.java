package com.example.COFFEEHOUSE.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ingredients")
public class IngredientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String unit;

    @Column(name = "stock_quantity")
    private Double stockQuantity;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

