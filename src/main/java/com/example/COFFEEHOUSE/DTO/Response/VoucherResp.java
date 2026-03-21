package com.example.COFFEEHOUSE.DTO.Response;

import com.example.COFFEEHOUSE.Enums.DiscountType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResp {
    private Long id;
    private String name;
    private Integer pointCost;
    private DiscountType discountType;
    private Double discountValue;
    private Double maxDiscountValue;
    private Double minBillTotal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer quantity;
    private Integer usedCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
