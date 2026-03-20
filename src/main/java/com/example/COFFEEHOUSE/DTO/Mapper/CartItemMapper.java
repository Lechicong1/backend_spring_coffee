package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Response.CartItemResp;
import com.example.COFFEEHOUSE.Entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CartItemMapper {
    CartItemEntity toEntity(CartItemReq request);

    void updateEntityFromRequest(CartItemReq request, @MappingTarget CartItemEntity entity);

    CartItemResp toDTO(CartItemEntity entity);

    List<CartItemResp> toDTOList(List<CartItemEntity> entities);
}

