package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeResp {
    private Long id;
    private Long productId;
    private String sizeName;
    private Long price;
}
