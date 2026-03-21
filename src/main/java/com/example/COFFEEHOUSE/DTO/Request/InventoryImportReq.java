package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryImportReq {
    private Long ingredientId;
    private BigDecimal importQuantity;
    private Long totalCost;
    private LocalDateTime importDate;
    private String note;
}
