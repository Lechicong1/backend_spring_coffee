package com.example.COFFEEHOUSE.DTO.Mapper;

import com.example.COFFEEHOUSE.DTO.Response.EmployeeResp;
import com.example.COFFEEHOUSE.Entity.EmployeeEntity;
import com.example.COFFEEHOUSE.Entity.RoleEntity;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EmployeeMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "address", source = "user.address")
    @Mapping(target = "roleId", source = "user.roleId")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "salary", source = "employee.salary")
    @Mapping(target = "hireDate", source = "employee.hireDate")
    EmployeeResp toDTO(UserEntity user, EmployeeEntity employee, RoleEntity role);
}

