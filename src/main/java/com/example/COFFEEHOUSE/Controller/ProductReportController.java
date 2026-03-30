package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.ProductReportFilterReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.ProductReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
