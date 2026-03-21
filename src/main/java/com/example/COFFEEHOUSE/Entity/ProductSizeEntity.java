package com.example.COFFEEHOUSE.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_sizes")
public class ProductSizeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "size_name", nullable = false)
    private String sizeName;

    @Column(columnDefinition = "bigint default 0")
    @Builder.Default
    private Long price = 0L;
}
