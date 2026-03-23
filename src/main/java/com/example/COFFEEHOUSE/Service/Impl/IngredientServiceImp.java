package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.IngredientMapper;
import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.Response.IngredientResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.IngredientRepo;
import com.example.COFFEEHOUSE.Service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientServiceImp implements IngredientService {
    private final IngredientMapper ingredientMapper;
    private final IngredientRepo ingredientRepo;

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
}

