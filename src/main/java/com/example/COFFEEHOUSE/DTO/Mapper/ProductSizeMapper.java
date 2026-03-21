package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.ProductSizeReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductSizeResp;
import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductSizeMapper {
    ProductSizeEntity toEntity(ProductSizeReq request);

    void updateEntityFromRequest(ProductSizeReq request, @MappingTarget ProductSizeEntity entity);

    List<ProductSizeResp> toDTOList(List<ProductSizeEntity> entities);

    ProductSizeResp toDTO(ProductSizeEntity entity);
}
