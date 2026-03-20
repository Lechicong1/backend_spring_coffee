package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String name);
}
