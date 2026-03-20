package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemReq {
    private Long userId;
    private Long productSizeId;
    private Integer quantity;
}

