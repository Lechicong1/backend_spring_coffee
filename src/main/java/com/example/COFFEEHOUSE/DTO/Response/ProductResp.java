package com.example.COFFEEHOUSE.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResp {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isActive;
    private List<ProductSizeResp> sizes;
}
