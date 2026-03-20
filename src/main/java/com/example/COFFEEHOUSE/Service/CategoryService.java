package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CategoryReq;
import com.example.COFFEEHOUSE.DTO.Response.CategoryResp;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryReq request);

    void updateCategory(Long id, CategoryReq request);

    void deleteCategory(Long id);

    List<CategoryResp> findAll();

    CategoryResp findById(Long id);

    List<CategoryResp> search(String keyword);
}

