package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.ProductReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData> create(
            @RequestPart("product") ProductReq request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        productService.createProduct(request, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Product created successfully")
                        .build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData> update(
            @PathVariable Long id, 
            @RequestPart("product") ProductReq request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        productService.updateProduct(id, request, image);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Product updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Product deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(productService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(productService.findAllForAdmin())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> search(
            @RequestParam("q") String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        var products = productService.search(keyword, categoryId);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(products)
                .build());
    }
}
