package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResp {
    private Long id;
    private String name;
    private String unit;
    private Double stockQuantity;
}

