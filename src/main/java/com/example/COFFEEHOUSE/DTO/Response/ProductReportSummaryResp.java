package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportSummaryResp {
    private Long totalVolume;
    private Long totalRevenue;
    private String bestSellerQty;
    private String bestSellerRev;
}

