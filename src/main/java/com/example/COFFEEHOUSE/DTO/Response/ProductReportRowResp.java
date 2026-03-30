package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportRowResp {
    private Long productId;
    private String productName;
    private String categoryName;
    private String imageUrl;
    private Long totalQuantity;
    private Long totalRevenue;
    private Double percent;
    private Double avgPrice;
}

