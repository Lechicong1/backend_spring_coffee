package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByOrderByNameAsc();
    @Query("SELECT c FROM CategoryEntity c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword% ORDER BY c.name ASC")
    List<CategoryEntity> search(@Param("keyword") String keyword);
}
