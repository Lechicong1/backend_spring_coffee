package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.UserResp;

import java.util.List;

public interface UserService {
    void createUser(UserReq userReq);
    void updateUser(Long id, UserReq userReq);
    void deleteUser(Long id);
    List<UserResp> findAll();
    UserResp findById(Long id);
}
