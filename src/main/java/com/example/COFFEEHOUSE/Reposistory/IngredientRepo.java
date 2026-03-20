package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepo extends JpaRepository<IngredientEntity, Long> {
    List<IngredientEntity> findAllByOrderByNameAsc();

    Optional<IngredientEntity> findByName(String name);

    Optional<IngredientEntity> findByNameAndIdNot(String name, Long id);

    @Query("SELECT i FROM IngredientEntity i WHERE i.name LIKE %:keyword% OR i.unit LIKE %:keyword% ORDER BY i.name")
    List<IngredientEntity> search(@Param("keyword") String keyword);
}

