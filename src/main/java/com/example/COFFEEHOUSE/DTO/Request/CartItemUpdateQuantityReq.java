package com.example.COFFEEHOUSE.DTO.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateQuantityReq {
    @Valid
    @Min(value = 1)
    private Integer quantity;
}
