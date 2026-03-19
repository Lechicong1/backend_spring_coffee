package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.UserResp;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseData> createUser(@RequestBody UserReq userReq) {
        userService.createUser(userReq);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("User created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateUser(@PathVariable Long id, @RequestBody UserReq userReq) {
        userService.updateUser(id, userReq);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("User updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("User deleted successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("User retrieved successfully")
                .data( userService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAllUsers() {

        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Users retrieved successfully")
                .data( userService.findAll())
                .build());
    }
}

