package com.example.COFFEEHOUSE.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReq {
    private String username;
    private String password;
    private String email;
    private String fullName;
    @JsonProperty("idRole") // accept JSON field named `idRole` as well as `roleId`
    private Long roleId;
    private Long points;
    private String address;
    private Long salary;
    private java.time.LocalDate hireDate;

    @Pattern(regexp = "^(0|\\+84)\\d{9,10}$", message = "Phone number is invalid")
    private String phone;
}
