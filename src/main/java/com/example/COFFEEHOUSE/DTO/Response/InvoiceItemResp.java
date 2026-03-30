package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResp {
    private Long orderItemId;
    private Long productSizeId;
    private String productName;
    private String sizeName;
    private Integer quantity;
    private Long unitPrice;
    private Long lineTotal;
    private String note;
}

