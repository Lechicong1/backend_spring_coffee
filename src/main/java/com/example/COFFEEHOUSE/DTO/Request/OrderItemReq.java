package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemReq {
    private Long productSizeId;
    private Integer quantity;
    private Long price;
    private String note;
    private String sizeName;
}

