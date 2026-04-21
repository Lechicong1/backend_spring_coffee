package com.example.COFFEEHOUSE.DTO.Request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemReq {
    @JsonProperty("product_size_id")
    @JsonAlias("productSizeId")
    private Long productSizeId;

    private Integer quantity;
    private Long price;
    private String note;

    @JsonProperty("size_name")
    @JsonAlias("sizeName")
    private String sizeName;
}
