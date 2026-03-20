package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepo extends JpaRepository<RecipeEntity, Long> {
    List<RecipeEntity> findByProductId(Long productId);

    List<RecipeEntity> findByIngredientId(Long ingredientId);
}

