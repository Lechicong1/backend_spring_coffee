package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.InventoryCheckReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryCheckResp;
import com.example.COFFEEHOUSE.Entity.InventoryCheckEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryCheckMapper {
    InventoryCheckEntity toEntity(InventoryCheckReq request);

    void updateEntityFromRequest(InventoryCheckReq request, @MappingTarget InventoryCheckEntity entity);

    InventoryCheckResp toDTO(InventoryCheckEntity entity);

    List<InventoryCheckResp> toDTOList(List<InventoryCheckEntity> entities);
}
