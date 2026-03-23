package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.EmployeeResp;
import java.util.List;

public interface EmployeeService {
    void createEmployee(UserReq userReq);
    void updateEmployee(Long id, UserReq userReq);
    void deleteEmployee(Long id);
    List<EmployeeResp> findAll();
    EmployeeResp findById(Long id);
}

