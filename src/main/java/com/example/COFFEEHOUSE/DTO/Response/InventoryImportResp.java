package com.example.COFFEEHOUSE.DTO.Response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryImportResp {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String ingredientUnit;
    private BigDecimal importQuantity;
    private Long totalCost;
    private LocalDateTime importDate;
    private String note;
}
