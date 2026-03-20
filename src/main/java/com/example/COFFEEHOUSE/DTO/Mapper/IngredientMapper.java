package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.Response.IngredientResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface IngredientMapper {
    IngredientEntity toEntity(IngredientReq request);

    void updateEntityFromRequest(IngredientReq request, @MappingTarget IngredientEntity entity);

    List<IngredientResp> toDTOList(List<IngredientEntity> entities);

    IngredientResp toDTO(IngredientEntity entity);
}

