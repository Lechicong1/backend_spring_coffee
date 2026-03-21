package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.InventoryImportReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryImportResp;
import com.example.COFFEEHOUSE.Entity.InventoryImportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InventoryImportMapper {
    InventoryImportEntity toEntity(InventoryImportReq request);

    void updateEntityFromRequest(InventoryImportReq request, @MappingTarget InventoryImportEntity entity);

    InventoryImportResp toDTO(InventoryImportEntity entity);

    List<InventoryImportResp> toDTOList(List<InventoryImportEntity> entities);
}
