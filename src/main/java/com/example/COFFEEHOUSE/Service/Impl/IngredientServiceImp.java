package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.IngredientMapper;
import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Response.IngredientResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import com.example.COFFEEHOUSE.Entity.RecipeEntity;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.IngredientRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductSizeRepo;
import com.example.COFFEEHOUSE.Reposistory.RecipeRepo;
import com.example.COFFEEHOUSE.Service.IngredientService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImp implements IngredientService {
    private final IngredientMapper ingredientMapper;
    private final IngredientRepo ingredientRepo;
    private final ProductSizeRepo productSizeRepo;
    private final RecipeRepo recipesRepo;

    @Override
    public void createIngredient(IngredientReq request) {

        if (ingredientRepo.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Ingredient with name '" + request.getName() + "' already exists");
        }

        Double initialStock = request.getStockQuantity() != null ? request.getStockQuantity() : 0.0;
        IngredientEntity entity = IngredientEntity.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .stockQuantity(initialStock)
                .build();
        ingredientRepo.save(entity);
    }

    @Override
    public void updateIngredient(Long id, IngredientReq request) {
        IngredientEntity existing = ingredientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        if (ingredientRepo.findByNameAndIdNot(request.getName(), id).isPresent()) {
            throw new DuplicateResourceException("Ingredient with name '" + request.getName() + "' already exists");
        }

        ingredientMapper.updateEntityFromRequest(request, existing);
        ingredientRepo.save(existing);
    }

    @Override
    public void deleteIngredient(Long id) { ingredientRepo.deleteById(id); }

    @Override
    public List<IngredientResp> findAll() {
        return ingredientMapper.toDTOList(ingredientRepo.findAllByOrderByNameAsc());
    }

    @Override
    public IngredientResp findById(Long id) {
        return ingredientRepo.findById(id)
                .map(ingredientMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
    }

    @Override
    public List<IngredientResp> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return ingredientMapper.toDTOList(ingredientRepo.search(keyword.trim()));
    }
    public void deductIngredients(List<OrderItemReq> items) {
        for (OrderItemReq item : items) {
            float multiplier = CommonUtils.getMultiplierBySize(item.getSizeName());
            ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm size này"));

            List<RecipeEntity> recipes = recipesRepo.findByProductId(productSize.getProductId());
            List<Long> ingredientIds = recipes.stream()
                    .map(RecipeEntity::getIngredientId)
                    .collect(Collectors.toList());

            List<IngredientEntity> ingredients = ingredientRepo.findAllById(ingredientIds);
            Map<Long, IngredientEntity> ingredientMap = ingredients.stream()
                    .collect(Collectors.toMap(IngredientEntity::getId, ing -> ing));

            for (RecipeEntity recipe : recipes) {
                IngredientEntity ingredient = ingredientMap.get(recipe.getIngredientId());
                double requiredAmount = recipe.getBaseAmount() * multiplier * item.getQuantity();
                ingredient.setStockQuantity(ingredient.getStockQuantity() - requiredAmount);
            }

            ingredientRepo.saveAll(ingredients);
        }
    }

    public void refundIngredients(List<OrderItemReq> items) {
        for (OrderItemReq item : items) {
            float multiplier = CommonUtils.getMultiplierBySize(item.getSizeName());
            ProductSizeEntity productSize = productSizeRepo.findById(item.getProductSizeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm size này"));

            List<RecipeEntity> recipes = recipesRepo.findByProductId(productSize.getProductId());
            List<Long> ingredientIds = recipes.stream()
                    .map(RecipeEntity::getIngredientId)
                    .collect(Collectors.toList());

            List<IngredientEntity> ingredients = ingredientRepo.findAllById(ingredientIds);
            Map<Long, IngredientEntity> ingredientMap = ingredients.stream()
                    .collect(Collectors.toMap(IngredientEntity::getId, ing -> ing));

            for (RecipeEntity recipe : recipes) {
                IngredientEntity ingredient = ingredientMap.get(recipe.getIngredientId());
                double refundAmount = recipe.getBaseAmount() * multiplier * item.getQuantity();
                ingredient.setStockQuantity(ingredient.getStockQuantity() + refundAmount);
            }

            ingredientRepo.saveAll(ingredients);
        }
    }
}
