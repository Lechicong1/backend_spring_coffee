package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.ProductReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void createProduct(ProductReq request, MultipartFile image);
    void updateProduct(Long id, ProductReq request, MultipartFile image);
    void deleteProduct(Long id);
    List<ProductResp> findAll();
    ProductResp findById(Long id);
    List<ProductResp> search(String keyword, Long categoryId);
}
