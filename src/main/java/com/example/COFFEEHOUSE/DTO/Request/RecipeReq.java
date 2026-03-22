package com.example.COFFEEHOUSE.DTO.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeReq {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotEmpty
    private List<@Valid IngredientItem> ingredientsList;

    @Getter
    @Setter
    public static class IngredientItem {
        @NotNull
        private Long ingredientId;
        @Positive
        private Double baseAmount;
    }
}



