package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.RoleReq;
import com.example.COFFEEHOUSE.DTO.Response.RoleResp;

import java.util.List;

public interface RoleService {
    void createRole(RoleReq request);
    void updateRole(Long id, RoleReq request);
    void deleteRole(Long id);
    List<RoleResp> findAll();
    RoleResp findById(Long id);
}

