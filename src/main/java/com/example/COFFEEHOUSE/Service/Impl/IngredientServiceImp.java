package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.IngredientMapper;
import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.Response.IngredientResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
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
        if (request.getName() == null || request.getName().trim().length() < 2 || request.getName().length() > 255) {
            throw new InvalidInputException("Ingredient name must be between 2 and 255 characters");
        }

        if (ingredientRepo.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Ingredient with name '" + request.getName() + "' already exists");
        }

        IngredientEntity entity = IngredientEntity.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .stockQuantity(0.0)
                .build();
        ingredientRepo.save(entity);
    }

    @Override
    public void updateIngredient(Long id, IngredientReq request) {
        IngredientEntity existing = ingredientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        if (request.getName() == null || request.getName().trim().length() < 2 || request.getName().length() > 255) {
            throw new InvalidInputException("Ingredient name must be between 2 and 255 characters");
        }

        if (ingredientRepo.findByNameAndIdNot(request.getName(), id).isPresent()) {
            throw new DuplicateResourceException("Ingredient with name '" + request.getName() + "' already exists");
        }

        ingredientMapper.updateEntityFromRequest(request, existing);
        ingredientRepo.save(existing);
    }

    @Override
    public void deleteIngredient(Long id) {
        if (!ingredientRepo.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepo.deleteById(id);
    }

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

