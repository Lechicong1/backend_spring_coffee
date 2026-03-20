package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.RecipeReq;
import com.example.COFFEEHOUSE.DTO.Response.RecipeResp;
import com.example.COFFEEHOUSE.Entity.RecipeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RecipeMapper {
    RecipeEntity toEntity(RecipeReq request);

    void updateEntityFromRequest(RecipeReq request, @MappingTarget RecipeEntity entity);

    RecipeResp toDTO(RecipeEntity entity);

    List<RecipeResp> toDTOList(List<RecipeEntity> entities);
}

