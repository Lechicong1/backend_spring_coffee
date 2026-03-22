package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String password;
    private String username;

}