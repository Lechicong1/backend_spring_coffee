package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeReq {
    private Long productId;
    private Long ingredientId;
    private Double baseAmount;
}



