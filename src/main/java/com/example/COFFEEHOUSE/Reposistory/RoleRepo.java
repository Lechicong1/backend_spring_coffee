package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
     RoleEntity findByName(String name);
}
