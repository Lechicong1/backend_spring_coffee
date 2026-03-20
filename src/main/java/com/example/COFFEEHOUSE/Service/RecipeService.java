package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.RecipeReq;
import com.example.COFFEEHOUSE.DTO.Response.RecipeResp;

import java.util.List;

public interface RecipeService {
    void createRecipe(RecipeReq request);

    void updateRecipe(Long id, RecipeReq request);

    void deleteRecipe(Long id);

    List<RecipeResp> findAll();

    RecipeResp findById(Long id);

    List<RecipeResp> findByProduct(Long productId);

    List<RecipeResp> findByIngredient(Long ingredientId);
}

