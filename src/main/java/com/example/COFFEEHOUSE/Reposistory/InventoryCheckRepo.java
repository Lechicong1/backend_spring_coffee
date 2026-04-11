package com.example.COFFEEHOUSE.Reposistory;

import com.example.COFFEEHOUSE.Entity.InventoryCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCheckRepo extends JpaRepository<InventoryCheckEntity, Long> {

    interface InventoryCheckDateRangeProjection {
        String getIngredient();

        LocalDate getCheckDate();

        BigDecimal getTotalTheory();

        BigDecimal getTotalActual();

        BigDecimal getTotalDifference();
    }

    /**
     * Lấy tất cả kiểm kho theo ngày (sắp xếp theo thời gian mới nhất)
     */
    @Query(value = "SELECT * FROM inventory_checks ic WHERE DATE(ic.checked_at) = :date ORDER BY ic.checked_at DESC",
            nativeQuery = true)
    List<InventoryCheckEntity> findByDate(@Param("date") LocalDate date);

    /**
     * Kiểm tra nguyên liệu đã được kiểm kho trong ngày chưa
     */
    @Query(value = "SELECT * FROM inventory_checks ic WHERE ic.ingredient = :ingredient AND DATE(ic.checked_at) = :date LIMIT 1",
            nativeQuery = true)
    Optional<InventoryCheckEntity> findByIngredientAndDate(@Param("ingredient") String ingredient,
            @Param("date") LocalDate date);

    /**
     * Kiểm tra nguyên liệu đã được kiểm kho hôm nay chưa
     */
    @Query(value = "SELECT * FROM inventory_checks ic WHERE ic.ingredient = :ingredient AND DATE(ic.checked_at) = CURRENT_DATE LIMIT 1",
            nativeQuery = true)
    Optional<InventoryCheckEntity> findByIngredientToday(@Param("ingredient") String ingredient);

    /**
     * Lấy tất cả kiểm kho (sắp xếp theo thời gian mới nhất)
     */
    List<InventoryCheckEntity> findAllByOrderByCheckedAtDesc();

    /**
     * Tìm kiếm theo tên nguyên liệu hoặc ghi chú
     */
    @Query("SELECT ic FROM InventoryCheckEntity ic WHERE ic.ingredient LIKE %:keyword% OR ic.note LIKE %:keyword% ORDER BY ic.checkedAt DESC")
    List<InventoryCheckEntity> search(@Param("keyword") String keyword);

    /**
     * Lấy kiểm kho theo khoảng thời gian
     */
    @Query("SELECT ic FROM InventoryCheckEntity ic WHERE ic.checkedAt BETWEEN :from AND :to ORDER BY ic.checkedAt DESC")
    List<InventoryCheckEntity> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Báo cáo thất thoát theo khoảng ngày (group theo nguyên liệu + ngày kiểm kho)
     */
    @Query(value = "SELECT ic.ingredient AS ingredient, "
            + "DATE(ic.checked_at) AS checkDate, "
            + "SUM(ic.theoryquantity) AS totalTheory, "
            + "SUM(ic.actualquantity) AS totalActual, "
            + "SUM(ic.difference) AS totalDifference "
            + "FROM inventory_checks ic "
            + "WHERE ic.checked_at >= :fromDateTime AND ic.checked_at < :toDateTimeExclusive "
            + "GROUP BY ic.ingredient, DATE(ic.checked_at) "
            + "ORDER BY DATE(ic.checked_at) DESC, ic.ingredient ASC",
            nativeQuery = true)
    List<InventoryCheckDateRangeProjection> getLossReportByDateRange(
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTimeExclusive") LocalDateTime toDateTimeExclusive);
}
