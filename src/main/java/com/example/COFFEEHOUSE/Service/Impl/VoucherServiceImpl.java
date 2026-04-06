package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.VoucherMapper;
import com.example.COFFEEHOUSE.DTO.Request.VoucherReq;
import com.example.COFFEEHOUSE.DTO.Response.VoucherResp;
import com.example.COFFEEHOUSE.Entity.UserEntity;
import com.example.COFFEEHOUSE.Entity.VoucherEntity;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Reposistory.VoucherRepo;
import com.example.COFFEEHOUSE.Service.VoucherService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepo voucherRepo;
    private final VoucherMapper voucherMapper;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public void createVoucher(VoucherReq request) {
        VoucherEntity entity = voucherMapper.toEntity(request);
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        voucherRepo.save(entity);
    }

    @Override
    @Transactional
    public void updateVoucher(Long id, VoucherReq request) {
        VoucherEntity entity = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucherMapper.updateEntityFromRequest(request, entity);
        voucherRepo.save(entity);
    }

    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        VoucherEntity entity = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        entity.setIsActive(false); // Soft delete parameter
        voucherRepo.save(entity);
    }

    @Override
    public VoucherResp getVoucherById(Long id) {
        VoucherEntity entity = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        return voucherMapper.toDTO(entity);
    }

    @Override
    public List<VoucherResp> findAll() {
        return voucherMapper.toDTOList(voucherRepo.findAll());
    }

    @Override
    public boolean isValidVoucher(Long id, Double billTotal) {
        VoucherEntity entity = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        if (entity.getIsActive() == null || !entity.getIsActive()) {
            return false;
        }

        if (entity.getQuantity() != null && entity.getQuantity() <= 0) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (entity.getStartDate() != null && now.isBefore(entity.getStartDate())) {
            return false;
        }
        if (entity.getEndDate() != null && now.isAfter(entity.getEndDate())) {
            return false;
        }

        if (entity.getMinBillTotal() != null && billTotal != null && billTotal < entity.getMinBillTotal()) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void decreaseVoucherQuantity(Long id) {
        VoucherEntity entity = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        if (entity.getQuantity() != null) {
            if (entity.getQuantity() <= 0) {
                throw new RuntimeException("Voucher is out of stock");
            }
            entity.setQuantity(entity.getQuantity() - 1);
        }

        if (entity.getUsedCount() != null) {
            entity.setUsedCount(entity.getUsedCount() + 1);
        } else {
            entity.setUsedCount(1);
        }

        voucherRepo.save(entity);
    }

    @Override
    public List<VoucherResp> getVouchersByUserPoints() {
        // Get current user ID from JWT token
        Long userId = CommonUtils.getIdUserFromToken();
        
        // Get user information
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get current user points
        Long userPoints = user.getPoints() != null ? user.getPoints() : 0L;
        
        // Get all active vouchers that user can afford based on points
        List<VoucherEntity> availableVouchers = voucherRepo.findAll().stream()
                .filter(voucher -> voucher.getIsActive() != null && voucher.getIsActive())
                .filter(voucher -> voucher.getPointCost() != null && voucher.getPointCost() <= userPoints)
                .filter(voucher -> voucher.getQuantity() != null && voucher.getQuantity() > 0)
                .filter(voucher -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                        return false;
                    }
                    if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return voucherMapper.toDTOList(availableVouchers);
    }

    @Override
    public List<VoucherResp> getVoucherByPhoneNumber(String phoneNumber) {
        Long phoneLong;
        try {
            phoneLong = Long.parseLong(phoneNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }

        UserEntity user = userRepo.findByPhoneNumber(phoneLong);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Long userPoints = user.getPoints() != null ? user.getPoints() : 0L;

        List<VoucherEntity> availableVouchers = voucherRepo.findAll().stream()
                .filter(voucher -> voucher.getIsActive() != null && voucher.getIsActive())
                .filter(voucher -> voucher.getPointCost() != null && voucher.getPointCost() <= userPoints)
                .filter(voucher -> voucher.getQuantity() != null && voucher.getQuantity() > 0)
                .filter(voucher -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
                        return false;
                    }
                    if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        return voucherMapper.toDTOList(availableVouchers);
    }
}
