package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody IngredientReq request) {
        ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Ingredient created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id, @RequestBody IngredientReq request) {
        ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Ingredient updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Ingredient deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Ingredient retrieved successfully")
                .data(ingredientService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Ingredients retrieved successfully")
                .data(ingredientService.findAll())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Ingredients retrieved successfully")
                .data(ingredientService.search(keyword))
                .build());
    }
}

