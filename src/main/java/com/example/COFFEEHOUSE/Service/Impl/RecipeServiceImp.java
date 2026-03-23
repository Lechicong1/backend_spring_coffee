package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.RecipeMapper;
import com.example.COFFEEHOUSE.DTO.Request.RecipeReq;
import com.example.COFFEEHOUSE.DTO.Response.RecipeResp;
import com.example.COFFEEHOUSE.Entity.RecipeEntity;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.RecipeRepo;
import com.example.COFFEEHOUSE.Service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeServiceImp implements RecipeService {
    private final RecipeMapper recipeMapper;
    private final RecipeRepo recipeRepo;

    @Override
    public void createRecipe(RecipeReq request) {
        List<RecipeEntity> recipes = new ArrayList<>();
        for (RecipeReq.IngredientItem item : request.getIngredientsList()) {
            RecipeEntity recipe = new RecipeEntity();
            recipe.setProductId(request.getProductId());
            recipe.setIngredientId(item.getIngredientId());
            recipe.setBaseAmount(item.getBaseAmount());
            recipes.add(recipe);
        }
        recipeRepo.saveAll(recipes);
    }

    @Transactional
    public void updateRecipe(Long productId, RecipeReq request) {

        // 1. XÓA hết nguyên liệu cũ của product này
        recipeRepo.deleteByProductId(productId);

        // 2. Thêm lại từ request
        List<RecipeEntity> list = new ArrayList<>();

        for (RecipeReq.IngredientItem item : request.getIngredientsList()) {
            RecipeEntity entity = new RecipeEntity();

            entity.setProductId(productId);
            entity.setIngredientId(item.getIngredientId());
            entity.setBaseAmount(item.getBaseAmount());

            list.add(entity);
        }

        recipeRepo.saveAll(list);
    }

    @Override
    public void deleteRecipe(Long id) {

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
}

