package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.InventoryCheckReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.InventoryCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/inventory-checks")
@RequiredArgsConstructor
public class InventoryCheckController {

    private final InventoryCheckService inventoryCheckService;

    /**
     * Tạo mới kiểm kho (LƯU MỚI)
     * Sẽ báo lỗi nếu nguyên liệu đã được kiểm kho trong ngày
     */
    @PostMapping
    public ResponseEntity<ResponseData> create(@RequestBody InventoryCheckReq request) {
        inventoryCheckService.createCheck(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Lưu kiểm kho thành công")
                        .build());
    }

    /**
     * Cập nhật kiểm kho (SỬA)
     * Sử dụng UPSERT logic - nếu đã có record trong ngày thì update, chưa có thì
     * insert
     */
    @PutMapping
    public ResponseEntity<ResponseData> update(@RequestBody InventoryCheckReq request) {
        inventoryCheckService.updateCheck(request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cập nhật kiểm kho thành công")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> delete(@PathVariable Long id) {
        inventoryCheckService.deleteCheck(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Xóa kiểm kho thành công")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Lấy kiểm kho thành công")
                .data(inventoryCheckService.findById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAll() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Lấy danh sách kiểm kho thành công")
                .data(inventoryCheckService.findAll())
                .build());
    }

    /**
     * Lấy danh sách kiểm kho theo ngày
     * Bao gồm cả những nguyên liệu chưa được kiểm kho (để hiển thị trong bảng)
     *
     * @param date Ngày cần xem (format: yyyy-MM-dd), mặc định là hôm nay
     */
    @GetMapping("/by-date")
    public ResponseEntity<ResponseData> getByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Lấy danh sách kiểm kho theo ngày thành công")
                .data(inventoryCheckService.getInventoryCheckDataByDate(targetDate))
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Tìm kiếm kiểm kho thành công")
                .data(inventoryCheckService.search(keyword))
                .build());
    }
}
