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
public class OrderDetailResp {
    private String orderCode;
    private List<OrderItemResp> items;
    private Long totalAmount; // sum of line totals (no shipping for modal)
}

