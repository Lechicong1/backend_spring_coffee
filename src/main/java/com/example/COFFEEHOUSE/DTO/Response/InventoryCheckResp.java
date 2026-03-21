package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCheckResp {
    private Long id;

    /**
     * Tên nguyên liệu
     */
    private String ingredient;

    /**
     * Đơn vị tính (lấy từ bảng ingredients)
     */
    private String unit;

    /**
     * Số lượng lý thuyết (lấy từ stock_quantity của ingredients)
     */
    private BigDecimal theoryQuantity;

    /**
     * Số lượng thực tế kiểm đếm
     */
    private BigDecimal actualQuantity;

    /**
     * Chênh lệch = actualQuantity - theoryQuantity
     */
    private BigDecimal difference;

    /**
     * Phần trăm chênh lệch
     */
    private BigDecimal percentDifference;

    /**
     * Trạng thái: OK, WARNING, CRITICAL
     */
    private String status;

    /**
     * Ghi chú
     */
    private String note;

    /**
     * Thời gian kiểm kho
     */
    private LocalDateTime checkedAt;
}
