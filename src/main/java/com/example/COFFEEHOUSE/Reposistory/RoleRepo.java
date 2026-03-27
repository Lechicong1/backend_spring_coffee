package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findFirstByName(String name);

    RoleEntity findByName(String name);
}
