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
    @Query(value = "SELECT p.id AS id, p.name AS name, c.name AS categoryName, p.image_url AS imageUrl, "
            + "SUM(oi.quantity) AS totalQuantity, SUM(oi.quantity * oi.price_at_purchase) AS totalRevenue, "
            + "0.0 AS costPrice, 0.0 AS profit "
            + "FROM order_items oi "
            + "JOIN orders o ON oi.order_id = o.id "
            + "JOIN product_sizes ps ON oi.product_size_id = ps.id "
            + "JOIN products p ON ps.product_id = p.id "
            + "JOIN categories c ON p.category_id = c.id "
            + "WHERE o.status = :status "
            + "AND o.created_at BETWEEN :fromDate AND :toDate "
            + "AND (:categoryId = 'all' OR CAST(p.category_id AS TEXT) = CAST(:categoryIdLong AS TEXT)) "
            + "GROUP BY p.id, p.name, c.name, p.image_url "
            + "ORDER BY SUM(oi.quantity * oi.price_at_purchase) DESC",
            nativeQuery = true)
    List<Object[]> fetchReportRowsNative(
            @Param("status") String status,
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
