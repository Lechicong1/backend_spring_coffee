package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.ProductReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductResp;
import com.example.COFFEEHOUSE.Entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {
    ProductEntity toEntity(ProductReq request);

    void updateEntityFromRequest(ProductReq request, @MappingTarget ProductEntity entity);

    List<ProductResp> toDTOList(List<ProductEntity> entities);

    ProductResp toDTO(ProductEntity entity);
}
