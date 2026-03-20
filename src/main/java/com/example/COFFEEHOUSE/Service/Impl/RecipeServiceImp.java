package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.RecipeMapper;
import com.example.COFFEEHOUSE.DTO.Request.RecipeReq;
import com.example.COFFEEHOUSE.DTO.Response.RecipeResp;
import com.example.COFFEEHOUSE.Entity.RecipeEntity;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.RecipeRepo;
import com.example.COFFEEHOUSE.Service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImp implements RecipeService {
    private final RecipeMapper recipeMapper;
    private final RecipeRepo recipeRepo;

    @Override
    public void createRecipe(RecipeReq request) {
        validateRecipeRequest(request);

        RecipeEntity entity = RecipeEntity.builder()
                .productId(request.getProductId())
                .ingredientId(request.getIngredientId())
                .baseAmount(request.getBaseAmount())
                .build();
        recipeRepo.save(entity);
    }

    @Override
    public void updateRecipe(Long id, RecipeReq request) {
        RecipeEntity existing = recipeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        validateRecipeRequest(request);

        existing.setProductId(request.getProductId());
        existing.setIngredientId(request.getIngredientId());
        existing.setBaseAmount(request.getBaseAmount());

        recipeRepo.save(existing);
    }

    @Override
    public void deleteRecipe(Long id) {
        if (!recipeRepo.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepo.deleteById(id);
    }

    @Override
    public List<RecipeResp> findAll() {
        return recipeMapper.toDTOList(recipeRepo.findAll());
    }

    @Override
    public RecipeResp findById(Long id) {
        return recipeRepo.findById(id)
                .map(recipeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    @Override
    public List<RecipeResp> findByProduct(Long productId) {
        return recipeMapper.toDTOList(recipeRepo.findByProductId(productId));
    }

    @Override
    public List<RecipeResp> findByIngredient(Long ingredientId) {
        return recipeMapper.toDTOList(recipeRepo.findByIngredientId(ingredientId));
    }

    private void validateRecipeRequest(RecipeReq request) {
        if (request.getProductId() == null) {
            throw new InvalidInputException("Product ID is required");
        }
        if (request.getIngredientId() == null) {
            throw new InvalidInputException("Ingredient ID is required");
        }
        if (request.getBaseAmount() == null || request.getBaseAmount() <= 0) {
            throw new InvalidInputException("Base amount must be greater than 0");
        }
    }
}

