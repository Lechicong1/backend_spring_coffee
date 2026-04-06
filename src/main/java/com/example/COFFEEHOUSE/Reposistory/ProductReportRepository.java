package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.DTO.Response.ProductReportRowResp;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductReportRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query("SELECT new com.example.COFFEEHOUSE.DTO.Response.ProductReportRowResp("
            + " p.id, p.name, c.name, p.imageUrl, SUM(oi.quantity), SUM(oi.quantity * oi.priceAtPurchase), 0D, 0D)"
            + " FROM OrderItemEntity oi"
            + " JOIN OrderEntity o ON oi.orderId = o.id"
            + " JOIN ProductSizeEntity ps ON oi.productSizeId = ps.id"
            + " JOIN ProductEntity p ON ps.productId = p.id"
            + " JOIN CategoryEntity c ON p.categoryId = c.id"
            + " WHERE o.status = :status"
            + " AND o.createdAt BETWEEN :fromDate AND :toDate"
            + " AND (:categoryId = 'all' OR p.categoryId = :categoryIdLong)"
            + " GROUP BY p.id, p.name, c.name, p.imageUrl"
            + " ORDER BY SUM(oi.quantity * oi.priceAtPurchase) DESC")
    List<ProductReportRowResp> fetchReportRows(
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("categoryId") String categoryId,
            @Param("categoryIdLong") Long categoryIdLong);

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.priceAtPurchase), 0L) FROM OrderItemEntity oi "
            + "JOIN OrderEntity o ON oi.orderId = o.id "
            + "WHERE o.status = 'COMPLETED' "
            + "AND o.createdAt BETWEEN :startDate AND :endDate "
            + "AND (:categoryId IS NULL OR EXISTS (SELECT 1 FROM ProductSizeEntity ps JOIN ProductEntity p ON ps.productId = p.id "
            + "WHERE ps.id = oi.productSizeId AND p.categoryId = :categoryId))")
    Long getTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("categoryId") Long categoryId);

    @Query("SELECT COALESCE(SUM(ii.totalCost), 0L) FROM InventoryImportEntity ii "
            + "WHERE ii.importDate BETWEEN :startDate AND :endDate")
    Long getInventoryExpense(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.salary), 0L) FROM EmployeeEntity e")
    Long getSalaryExpense();
}
