package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByOrderCode(String orderCode);
    List<OrderEntity> findByUserId(Long userId);
}