package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.InventoryImportMapper;
import com.example.COFFEEHOUSE.DTO.Request.InventoryImportReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryImportResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import com.example.COFFEEHOUSE.Entity.InventoryImportEntity;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.IngredientRepo;
import com.example.COFFEEHOUSE.Reposistory.InventoryImportRepo;
import com.example.COFFEEHOUSE.Service.InventoryImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryImportServiceImp implements InventoryImportService {

    private final InventoryImportRepo inventoryImportRepo;
    private final IngredientRepo ingredientRepo;
    private final InventoryImportMapper inventoryImportMapper;

    @Override
    @Transactional
    public void createImport(InventoryImportReq request) {
        // Validation
        validateImportRequest(request);

        // Check if ingredient exists
        IngredientEntity ingredient = ingredientRepo.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + request.getIngredientId()));

        // Create import entity
        InventoryImportEntity importEntity = InventoryImportEntity.builder()
                .ingredientId(request.getIngredientId())
                .importQuantity(request.getImportQuantity())
                .totalCost(request.getTotalCost())
                .importDate(request.getImportDate() != null ? request.getImportDate() : LocalDateTime.now())
                .note(request.getNote())
                .build();

        inventoryImportRepo.save(importEntity);

        // Update ingredient stock quantity (ADD)
        Double currentStock = ingredient.getStockQuantity() != null ? ingredient.getStockQuantity() : 0.0;
        ingredient.setStockQuantity(currentStock + request.getImportQuantity().doubleValue());
        ingredient.setUpdatedAt(LocalDateTime.now());
        ingredientRepo.save(ingredient);
    }

    @Override
    @Transactional
    public void updateImport(Long id, InventoryImportReq request) {
        // Validation
        validateImportRequest(request);

        // Get old import
        InventoryImportEntity oldImport = inventoryImportRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Import not found with id: " + id));

        // Check if new ingredient exists
        IngredientEntity newIngredient = ingredientRepo.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + request.getIngredientId()));

        Long oldIngredientId = oldImport.getIngredientId();
        BigDecimal oldQuantity = oldImport.getImportQuantity();
        BigDecimal newQuantity = request.getImportQuantity();

        // Update import entity
        oldImport.setIngredientId(request.getIngredientId());
        oldImport.setImportQuantity(request.getImportQuantity());
        oldImport.setTotalCost(request.getTotalCost());
        if (request.getImportDate() != null) {
            oldImport.setImportDate(request.getImportDate());
        }
        oldImport.setNote(request.getNote());
        inventoryImportRepo.save(oldImport);

        // Adjust stock quantities
        if (oldIngredientId.equals(request.getIngredientId())) {
            // CASE 1: Same ingredient - adjust the difference
            // stock = stock - old_qty + new_qty
            Double currentStock = newIngredient.getStockQuantity() != null ? newIngredient.getStockQuantity() : 0.0;
            double adjustment = newQuantity.doubleValue() - oldQuantity.doubleValue();
            newIngredient.setStockQuantity(currentStock + adjustment);
            newIngredient.setUpdatedAt(LocalDateTime.now());
            ingredientRepo.save(newIngredient);
        } else {
            // CASE 2: Different ingredient
            // Old ingredient: subtract old quantity
            IngredientEntity oldIngredient = ingredientRepo.findById(oldIngredientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Old ingredient not found with id: " + oldIngredientId));
            Double oldIngredientStock = oldIngredient.getStockQuantity() != null ? oldIngredient.getStockQuantity() : 0.0;
            oldIngredient.setStockQuantity(oldIngredientStock - oldQuantity.doubleValue());
            oldIngredient.setUpdatedAt(LocalDateTime.now());
            ingredientRepo.save(oldIngredient);

            // New ingredient: add new quantity
            Double newIngredientStock = newIngredient.getStockQuantity() != null ? newIngredient.getStockQuantity() : 0.0;
            newIngredient.setStockQuantity(newIngredientStock + newQuantity.doubleValue());
            newIngredient.setUpdatedAt(LocalDateTime.now());
            ingredientRepo.save(newIngredient);
        }
    }

    @Override
    @Transactional
    public void deleteImport(Long id) {
        // Get import before deleting
        InventoryImportEntity importEntity = inventoryImportRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Import not found with id: " + id));

        // Delete import
        inventoryImportRepo.delete(importEntity);

        // Subtract quantity from ingredient stock (fixing the bug mentioned in the flow)
        IngredientEntity ingredient = ingredientRepo.findById(importEntity.getIngredientId()).orElse(null);
        if (ingredient != null) {
            Double currentStock = ingredient.getStockQuantity() != null ? ingredient.getStockQuantity() : 0.0;
            ingredient.setStockQuantity(currentStock - importEntity.getImportQuantity().doubleValue());
            ingredient.setUpdatedAt(LocalDateTime.now());
            ingredientRepo.save(ingredient);
        }
    }

    @Override
    public List<InventoryImportResp> findAll() {
        List<InventoryImportEntity> imports = inventoryImportRepo.findAllByOrderByImportDateDesc();
        return imports.stream()
                .map(this::toResponseWithIngredient)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryImportResp findById(Long id) {
        InventoryImportEntity importEntity = inventoryImportRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Import not found with id: " + id));
        return toResponseWithIngredient(importEntity);
    }

    @Override
    public List<InventoryImportResp> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String trimmedKeyword = keyword.trim();

        // Search by note
        List<InventoryImportEntity> byNote = inventoryImportRepo.searchByNote(trimmedKeyword);

        // Search by ingredient name
        List<IngredientEntity> matchingIngredients = ingredientRepo.search(trimmedKeyword);
        List<Long> ingredientIds = matchingIngredients.stream()
                .map(IngredientEntity::getId)
                .collect(Collectors.toList());

        List<InventoryImportEntity> byIngredient = ingredientIds.isEmpty()
                ? List.of()
                : inventoryImportRepo.findByIngredientIds(ingredientIds);

        // Merge results and remove duplicates
        java.util.Set<Long> seenIds = new java.util.HashSet<>();
        List<InventoryImportResp> results = new java.util.ArrayList<>();

        for (InventoryImportEntity entity : byNote) {
            if (seenIds.add(entity.getId())) {
                results.add(toResponseWithIngredient(entity));
            }
        }
        for (InventoryImportEntity entity : byIngredient) {
            if (seenIds.add(entity.getId())) {
                results.add(toResponseWithIngredient(entity));
            }
        }

        return results;
    }

    private InventoryImportResp toResponseWithIngredient(InventoryImportEntity entity) {
        IngredientEntity ingredient = ingredientRepo.findById(entity.getIngredientId()).orElse(null);

        return InventoryImportResp.builder()
                .id(entity.getId())
                .ingredientId(entity.getIngredientId())
                .ingredientName(ingredient != null ? ingredient.getName() : null)
                .ingredientUnit(ingredient != null ? ingredient.getUnit() : null)
                .importQuantity(entity.getImportQuantity())
                .totalCost(entity.getTotalCost())
                .importDate(entity.getImportDate())
                .note(entity.getNote())
                .build();
    }

    private void validateImportRequest(InventoryImportReq request) {
        if (request.getIngredientId() == null) {
            throw new InvalidInputException("Ingredient ID is required");
        }
        if (request.getImportQuantity() == null || request.getImportQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Import quantity must be greater than 0");
        }
        if (request.getTotalCost() == null || request.getTotalCost() < 0) {
            throw new InvalidInputException("Total cost must be greater than or equal to 0");
        }
    }
}
