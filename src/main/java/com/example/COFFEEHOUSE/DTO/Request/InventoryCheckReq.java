package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InventoryCheckReq {
    private String ingredient;
    private BigDecimal actualQuantity;
    private String note;
}
