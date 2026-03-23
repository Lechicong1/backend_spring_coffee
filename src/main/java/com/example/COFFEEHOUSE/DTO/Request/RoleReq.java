package com.example.COFFEEHOUSE.DTO.Request;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleReq {
    @Valid
    private String name;
    private String description;
}

