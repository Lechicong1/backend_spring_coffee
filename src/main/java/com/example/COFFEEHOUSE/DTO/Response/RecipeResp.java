package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResp {
    private Long id;
    private Long productId;
    private Long ingredientId;
    private Double baseAmount;
}

