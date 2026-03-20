package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.CategoryReq;
import com.example.COFFEEHOUSE.DTO.Response.CategoryResp;
import com.example.COFFEEHOUSE.Entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryReq request);

    void updateEntityFromRequest(CategoryReq request, @MappingTarget CategoryEntity entity);

    List<CategoryResp> toDTOList(List<CategoryEntity> entities);

    CategoryResp toDTO(CategoryEntity entity);
}

