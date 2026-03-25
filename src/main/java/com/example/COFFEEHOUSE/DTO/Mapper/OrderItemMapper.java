package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Response.OrderItemResp;
import com.example.COFFEEHOUSE.Entity.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResp toResponse(OrderItemEntity entity);
    OrderItemEntity toEntity(OrderItemResp response);
}

