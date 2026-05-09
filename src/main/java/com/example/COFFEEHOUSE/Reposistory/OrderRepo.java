package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.OrderEntity;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByOrderCode(String orderCode);
    List<OrderEntity> findByUserId(Long userId);
    List<OrderEntity> findByStatus(OrderStatus status);

    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<OrderEntity> findCompletedOrdersByDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}