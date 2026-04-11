package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.UserMapper;
import com.example.COFFEEHOUSE.DTO.Request.UserReq;
import com.example.COFFEEHOUSE.DTO.Response.UserResp;
import com.example.COFFEEHOUSE.Entity.RoleEntity;
import com.example.COFFEEHOUSE.Enums.ROLE;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Reposistory.RoleRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Service.UserService;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    @Override
    @Transactional
    public void createUser(UserReq userReq) {
        RoleEntity roleDefault = roleRepo.findByName(ROLE.USER.name());
        if(roleDefault == null) {
            throw new ResourceNotFoundException("Default role not found: " + ROLE.USER.name());
        }
        
        if (userRepo.existsByUsername(userReq.getUsername())) {
            throw new DuplicateResourceException("Username đã tồn tại");
        }
        if (userReq.getEmail() != null && userRepo.existsByEmail(userReq.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }
        if (userReq.getPhoneNumber() != null && userRepo.existsByPhoneNumber(userReq.getPhoneNumber())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }

        Long roleId = userReq.getRoleId() == null ? roleDefault.getId() : userReq.getRoleId();

        UserEntity user = UserEntity.builder()
                .username(userReq.getUsername())
                .password(passwordEncoder.encode(userReq.getPassword()))
                .email(userReq.getEmail())
                .fullName(userReq.getFullName())
                .roleId(roleId)
                .build();
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserReq userReq) {
        UserEntity existingUser = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Kiểm tra trùng lặp khi cập nhật, loại trừ bản ghi hiện tại
        if (userReq.getEmail() != null && userRepo.existsByEmailAndIdNot(userReq.getEmail(), id)) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }
        if (userReq.getPhoneNumber() != null && userRepo.existsByPhoneNumberAndIdNot(userReq.getPhoneNumber(), id)) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }

        userMapper.updateEntityFromRequest(userReq, existingUser);

        userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepo.deleteById(id);
    }

    @Override
    public List<UserResp> findAll() {
        return userMapper.toDTOList(userRepo.findAllCustomer());
    }

    @Override
    public UserResp findById(Long id) {
        return userRepo.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResp getCurrentUser() {
        Long userId = CommonUtils.getIdUserFromToken();
        return findById(userId);
    }

    @Override
    @Transactional
    public void updateCurrentUser(UserReq userReq) {
        Long userId = CommonUtils.getIdUserFromToken();
        updateUser(userId, userReq);
    }
}
