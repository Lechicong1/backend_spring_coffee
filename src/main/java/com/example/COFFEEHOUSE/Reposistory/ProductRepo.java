package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    List<ProductEntity> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name);
}
