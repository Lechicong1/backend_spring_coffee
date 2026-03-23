package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.EmployeeMapper;
import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.EmployeeResp;
import com.example.COFFEEHOUSE.Entity.EmployeeEntity;
import com.example.COFFEEHOUSE.Entity.RoleEntity;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import com.example.COFFEEHOUSE.Enums.ROLE;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.EmployeeRepo;
import com.example.COFFEEHOUSE.Reposistory.RoleRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImp implements EmployeeService {
    private final UserRepo userRepo;
    private final EmployeeRepo employeeRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public void createEmployee(UserReq userReq) {
        // Mặc định nếu không truyền roleId thì tìm role nhân viên (ví dụ MANAGER hoặc STAFF)
        // Ở đây giả sử roleId được truyền từ client
        RoleEntity role = roleRepo.findById(userReq.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userReq.getRoleId()));

        UserEntity user = UserEntity.builder()
                .username(userReq.getUsername())
                .password(passwordEncoder.encode(userReq.getPassword()))
                .email(userReq.getEmail())
                .fullName(userReq.getFullName())
                .address(userReq.getAddress())
                .roleId(role.getId())
                .build();
        userRepo.save(user);

        EmployeeEntity employee = EmployeeEntity.builder()
                .userId(user.getId())
                .salary(userReq.getSalary())
                .hireDate(userReq.getHireDate() != null ? userReq.getHireDate() : LocalDate.now())
                .build();
        employeeRepo.save(employee);
    }

    @Override
    @Transactional
    public void updateEmployee(Long id, UserReq userReq) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        user.setFullName(userReq.getFullName());
        user.setEmail(userReq.getEmail());
        user.setAddress(userReq.getAddress());
        if (userReq.getRoleId() != null) {
            user.setRoleId(userReq.getRoleId());
        }
        userRepo.save(user);

        EmployeeEntity employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee details not found for user id: " + id));

        if (userReq.getSalary() != null) {
            employee.setSalary(userReq.getSalary());
        }
        if (userReq.getHireDate() != null) {
            employee.setHireDate(userReq.getHireDate());
        }
        employeeRepo.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepo.deleteById(id);
        userRepo.deleteById(id);
    }

    @Override
    public List<EmployeeResp> findAll() {
        return employeeRepo.findAll().stream().map(emp -> {
            UserEntity user = userRepo.findById(emp.getUserId()).orElse(null);
            RoleEntity role = user != null ? roleRepo.findById(user.getRoleId()).orElse(null) : null;
            return employeeMapper.toDTO(user, emp, role);
        }).collect(Collectors.toList());
    }

    @Override
    public EmployeeResp findById(Long id) {
        EmployeeEntity emp = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        UserEntity user = userRepo.findById(id).orElse(null);
        RoleEntity role = user != null ? roleRepo.findById(user.getRoleId()).orElse(null) : null;
        return employeeMapper.toDTO(user, emp, role);
    }
}
