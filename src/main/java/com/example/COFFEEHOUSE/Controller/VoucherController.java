package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.VoucherReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody VoucherReq request) {
        voucherService.createVoucher(request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Voucher created successfully")
                .data(null)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> update(@PathVariable Long id, @RequestBody VoucherReq request) {
        voucherService.updateVoucher(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Voucher updated successfully")
                .data(null)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Voucher deleted successfully")
                .data(null)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Voucher retrieved successfully")
                .data(voucherService.getVoucherById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Vouchers retrieved successfully")
                .data(voucherService.findAll())
                .build());
    }
}
