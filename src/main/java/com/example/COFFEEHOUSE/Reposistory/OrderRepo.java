package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderCode(String orderCode);
    List<OrderEntity> findByUserId(Long userId);
    List<OrderEntity> findByStatusOrderByCreatedAtDesc(String status);
}

