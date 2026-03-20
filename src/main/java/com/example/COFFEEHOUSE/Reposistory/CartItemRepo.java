package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserId(Long userId);

    Optional<CartItemEntity> findByUserIdAndProductSizeId(Long userId, Long productSizeId);

    void deleteByUserId(Long userId);
}
