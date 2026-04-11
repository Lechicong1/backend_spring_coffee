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
            + " p.id, p.name, c.name, p.imageUrl, "
            + " CAST(SUM(oi.quantity) AS long), "
            + " CAST(SUM(oi.quantity * oi.priceAtPurchase) AS long), "
            + " CAST(0.0 AS double), "
            + " CAST(0.0 AS double))"
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
    List<ProductReportRowResp> fetchReportRowsNative(
            @Param("status") OrderStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("categoryId") String categoryId,
            @Param("categoryIdLong") Long categoryIdLong);

    @Query(value = "SELECT COALESCE(SUM(oi.quantity * oi.price_at_purchase), 0) FROM order_items oi "
            + "JOIN orders o ON oi.order_id = o.id "
            + "WHERE o.status = 'COMPLETED' "
            + "AND o.created_at BETWEEN :startDate AND :endDate "
            + "AND (:categoryId IS NULL OR EXISTS (SELECT 1 FROM product_sizes ps JOIN products p ON ps.product_id = p.id "
            + "WHERE ps.id = oi.product_size_id AND p.category_id = :categoryId))",
            nativeQuery = true)
    Long getTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("categoryId") Long categoryId);

    @Query(value = "SELECT COALESCE(SUM(ii.total_cost), 0) FROM inventory_imports ii "
            + "WHERE ii.import_date BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    Long getInventoryExpense(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COALESCE(SUM(e.salary), 0) FROM employees e",
            nativeQuery = true)
    Long getSalaryExpense();
}
