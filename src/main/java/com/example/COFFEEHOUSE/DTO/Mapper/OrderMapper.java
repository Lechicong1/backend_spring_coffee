package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Response.OrderResp;
import com.example.COFFEEHOUSE.Entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResp toResponse(OrderEntity entity);
    OrderEntity toEntity(OrderResp response);
}

