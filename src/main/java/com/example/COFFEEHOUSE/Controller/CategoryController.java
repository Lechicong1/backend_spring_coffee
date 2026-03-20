package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.CategoryReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody CategoryReq request) {
        categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Category created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id, @RequestBody CategoryReq request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Category updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Category deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Category retrieved successfully")
                .data(categoryService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Categories retrieved successfully")
                .data(categoryService.findAll())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Categories retrieved successfully")
                .data(categoryService.search(keyword))
                .build());
    }
}

