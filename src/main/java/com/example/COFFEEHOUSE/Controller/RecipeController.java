package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.RecipeReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@Valid @RequestBody RecipeReq request) {
        recipeService.createRecipe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Recipe created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id,@Valid @RequestBody RecipeReq request) {
        recipeService.updateRecipe(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipe updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipe deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipe retrieved successfully")
                .data(recipeService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipes retrieved successfully")
                .data(recipeService.findAll())
                .build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseData> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipes retrieved successfully")
                .data(recipeService.findByProduct(productId))
                .build());
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<ResponseData> getByIngredient(@PathVariable Long ingredientId) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Recipes retrieved successfully")
                .data(recipeService.findByIngredient(ingredientId))
                .build());
    }
}

