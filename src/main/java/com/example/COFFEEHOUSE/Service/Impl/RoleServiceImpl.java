package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Request.RoleReq;
import com.example.COFFEEHOUSE.DTO.Response.RoleResp;
import com.example.COFFEEHOUSE.Entity.RoleEntity;
import com.example.COFFEEHOUSE.Reposistory.RoleRepo;
import com.example.COFFEEHOUSE.Service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;

    @Override
    public void createRole(RoleReq request) {
        RoleEntity role = RoleEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        roleRepo.save(role);
    }

    @Override
    public void updateRole(Long id, RoleReq request) {
        RoleEntity role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        roleRepo.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepo.deleteById(id);
    }

    @Override
    public List<RoleResp> findAll() {
        return roleRepo.findAll().stream()
                .map(this::mapToResp)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResp findById(Long id) {
        RoleEntity role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return mapToResp(role);
    }

    private RoleResp mapToResp(RoleEntity role) {
        return RoleResp.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}

