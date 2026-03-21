package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.VoucherReq;
import com.example.COFFEEHOUSE.DTO.Response.VoucherResp;
import com.example.COFFEEHOUSE.Entity.VoucherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoucherMapper {
    VoucherEntity toEntity(VoucherReq request);

    void updateEntityFromRequest(VoucherReq request, @MappingTarget VoucherEntity entity);

    VoucherResp toDTO(VoucherEntity entity);

    List<VoucherResp> toDTOList(List<VoucherEntity> entities);
}
