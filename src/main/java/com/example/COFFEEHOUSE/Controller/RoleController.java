package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.RoleReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody RoleReq request) {
        roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Role created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id, @RequestBody RoleReq request) {
        roleService.updateRole(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Role updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Role deleted successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Roles retrieved successfully")
                .data(roleService.findAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Role retrieved successfully")
                .data(roleService.findById(id))
                .build());
    }
}
