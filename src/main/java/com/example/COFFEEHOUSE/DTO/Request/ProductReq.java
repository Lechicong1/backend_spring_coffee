package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductReq {
    private Long categoryId;
    private String name;
    private String description;
    private Boolean isActive = true;
    private List<ProductSizeReq> sizes;
}
