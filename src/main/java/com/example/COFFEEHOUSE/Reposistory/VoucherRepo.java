package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepo extends JpaRepository<VoucherEntity, Long> {
    Optional<VoucherEntity> findByName(String name);
}
