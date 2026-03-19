package com.example.COFFEEHOUSE.DTO.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReq {
    private String username;
    private String password;
    private String email;
    private String fullName;

}
