package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResp {
    private Long id;
    private Long userId;
    private Long productSizeId;
    private Integer quantity;
    private String productName;
    private String productImageUrl;
    private String sizeName;
    private Long price;
}
