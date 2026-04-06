package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.ProductReportFilterReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.ProductReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/product-report")
@RequiredArgsConstructor
public class ProductReportController {

    private final ProductReportService productReportService;

    @GetMapping("/data")
    public ResponseEntity<ResponseData> getReportData(@Valid @ModelAttribute ProductReportFilterReq filterReq) {
        ResponseData response = productReportService.getReportData(filterReq);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseData> getCategories() {
        return ResponseEntity.ok(productReportService.getCategories());
    }

    @GetMapping("/revenue")
    public ResponseEntity<ResponseData> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {
        ResponseData response = productReportService.getTotalRevenue(startDate, endDate, categoryId);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

        @GetMapping("/inventory-expense")
    public ResponseEntity<ResponseData> getInventoryExpense(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ResponseData response = productReportService.getInventoryExpense(startDate, endDate);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }

    @GetMapping("/salary-expense")
    public ResponseEntity<ResponseData> getSalaryExpense() {
        ResponseData response = productReportService.getSalaryExpense();
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
}
