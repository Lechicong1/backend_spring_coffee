package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.InventoryImportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryImportRepo extends JpaRepository<InventoryImportEntity, Long> {

    List<InventoryImportEntity> findAllByOrderByImportDateDesc();

    List<InventoryImportEntity> findByIngredientId(Long ingredientId);

    @Query("SELECT ii FROM InventoryImportEntity ii WHERE ii.note LIKE %:keyword% ORDER BY ii.importDate DESC")
    List<InventoryImportEntity> searchByNote(@Param("keyword") String keyword);

    @Query("SELECT ii FROM InventoryImportEntity ii WHERE ii.ingredientId IN :ingredientIds ORDER BY ii.importDate DESC")
    List<InventoryImportEntity> findByIngredientIds(@Param("ingredientIds") List<Long> ingredientIds);
}
