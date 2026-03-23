package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResp {
    private Long id;
    private Long productSizeId;
    private Integer quantity;
    private Long priceAtPurchase;
    private String note;
}

