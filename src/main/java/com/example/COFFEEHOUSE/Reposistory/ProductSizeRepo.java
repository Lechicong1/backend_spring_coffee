package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSizeRepo extends JpaRepository<ProductSizeEntity, Long> {
    List<ProductSizeEntity> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
