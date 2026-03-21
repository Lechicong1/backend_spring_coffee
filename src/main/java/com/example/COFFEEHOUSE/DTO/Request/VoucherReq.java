package com.example.COFFEEHOUSE.DTO.Request;

import com.example.COFFEEHOUSE.Enums.DiscountType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherReq {
    private String name;
    private Integer pointCost;
    private DiscountType discountType;
    private Double discountValue;
    private Double maxDiscountValue;
    private Double minBillTotal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer quantity;
    private Boolean isActive;
}
