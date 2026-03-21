package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.CategoryMapper;
import com.example.COFFEEHOUSE.DTO.Request.CategoryReq;
import com.example.COFFEEHOUSE.DTO.Response.CategoryResp;
import com.example.COFFEEHOUSE.Entity.CategoryEntity;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.CategoryRepo;
import com.example.COFFEEHOUSE.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepo categoryRepo;

    @Override
    public void createCategory(CategoryReq request) {
        // validate ngay trong request , su dung annotation @Valid
        if (request.getName() == null || request.getName().trim().length() < 2 || request.getName().length() > 255) {
            throw new InvalidInputException("Category name must be between 2 and 255 characters");
        }

        if (categoryRepo.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists");
        }

        CategoryEntity entity = CategoryEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        categoryRepo.save(entity);
    }

    @Override
    public void updateCategory(Long id, CategoryReq request) {
        CategoryEntity existing = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryMapper.updateEntityFromRequest(request, existing);
        categoryRepo.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public List<CategoryResp> findAll() {
        return categoryMapper.toDTOList(categoryRepo.findAllByOrderByNameAsc());
    }

    @Override
    public CategoryResp findById(Long id) {
        return categoryRepo.findById(id)
                .map(categoryMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public List<CategoryResp> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return categoryMapper.toDTOList(categoryRepo.search(keyword.trim()));
    }
}

