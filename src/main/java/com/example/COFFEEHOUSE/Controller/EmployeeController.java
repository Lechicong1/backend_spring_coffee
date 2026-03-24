package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.EmployeeResp;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ResponseData> createEmployee(@RequestBody UserReq userReq) {
        employeeService.createEmployee(userReq);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Employee created successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateEmployee(@PathVariable Long id, @RequestBody UserReq userReq) {
        employeeService.updateEmployee(id, userReq);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Employee update successfully")
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Employee delete successfully")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> findAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.builder()
                        .success(true)
                        .data(employeeService.findAll())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.builder()
                        .success(true)
                        .data(employeeService.findById(id))
                        .build());
    }
}
