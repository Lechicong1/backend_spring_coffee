package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.UserResp;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserEntity toEntity(UserReq request);
    void updateEntityFromRequest(UserReq request, @MappingTarget UserEntity entity);
    List<UserResp> toDTOList(List<UserEntity> entities);
    UserResp toDTO(UserEntity entity);
}
