package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.VoucherReq;
import com.example.COFFEEHOUSE.DTO.Response.VoucherResp;

import java.util.List;

public interface VoucherService {
    void createVoucher(VoucherReq request);
    void updateVoucher(Long id, VoucherReq request);
    void deleteVoucher(Long id);
    VoucherResp getVoucherById(Long id);
    List<VoucherResp> findAll();
}
