package com.example.COFFEEHOUSE.DTO.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemReq {
    @Valid
    private Long userId;
    @Valid
    private Long productSizeId;
    @Valid
    @Min(value = 1)
    private Integer quantity;
}

