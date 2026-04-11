package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String name);
    UserEntity findByPhoneNumber(String phoneNumber);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
    @Query("SELECT u FROM UserEntity u JOIN RoleEntity r ON u.roleId = r.id WHERE r.name = 'USER'")
    List<UserEntity> findAllCustomer();
}
