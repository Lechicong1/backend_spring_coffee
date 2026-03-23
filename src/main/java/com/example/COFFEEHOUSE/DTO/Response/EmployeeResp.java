package com.example.COFFEEHOUSE.DTO.Response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeResp {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String address;
    private Long roleId;
    private String roleName;
    private Long salary;
    private LocalDate hireDate;
    private Long phone ;
}
