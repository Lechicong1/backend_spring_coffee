package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Request.ProductReportFilterReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductReportDataResp;
import com.example.COFFEEHOUSE.DTO.Response.ProductReportRowResp;
import com.example.COFFEEHOUSE.DTO.Response.ProductReportSummaryResp;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Entity.CategoryEntity;
import com.example.COFFEEHOUSE.Enums.OrderStatus;
import com.example.COFFEEHOUSE.Reposistory.CategoryRepo;
import com.example.COFFEEHOUSE.Reposistory.ProductReportRepository;
import com.example.COFFEEHOUSE.Service.ProductReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
public class ProductReportServiceImpl implements ProductReportService {

    private final ProductReportRepository productReportRepository;
    private final CategoryRepo categoryRepo;

    @Override
    public ResponseData getReportData(ProductReportFilterReq filterReq) {
        validateCategory(filterReq.getCategory_id()); // Dùng tên biến mới

        LocalDate from = filterReq.getFrom_date();
        LocalDate to = filterReq.getTo_date();

        Long categoryIdLong = Objects.equals(filterReq.getCategory_id(), "all") ? null : Long.valueOf(filterReq.getCategory_id());

        List<Object[]> rawRows = productReportRepository.fetchReportRowsNative(
                OrderStatus.COMPLETED.name(),
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX),
                filterReq.getCategory_id(),
                categoryIdLong
        );

        List<ProductReportRowResp> rows = rawRows.stream().map(row -> ProductReportRowResp.builder()
                .productId(((Number) row[0]).longValue())
                .productName((String) row[1])
                .categoryName((String) row[2])
                .imageUrl((String) row[3])
                .totalQuantity(((Number) row[4]).longValue())
                .totalRevenue(((Number) row[5]).longValue())
                .percent((Double) row[6])
                .avgPrice((Double) row[7])
                .build()
        ).toList();

        return ResponseData.builder()
                .success(true)
                .data(calculateSummaryAndSort(rows, filterReq.getSort_by()))
                .build();
    }

    @Override
    public ResponseData getCategories() {
        List<CategoryEntity> categories = categoryRepo.findAll();
        return ResponseData.builder()
                .success(true)
                .data(categories)
                .build();
    }

    private void validateCategory(String categoryId) {
        if (Objects.equals(categoryId, "all")) {
            return;
        }
        Long id = Long.valueOf(categoryId);
        if (!categoryRepo.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
    }

    private ProductReportDataResp calculateSummaryAndSort(List<ProductReportRowResp> rows, String sortBy) {
        long totalVolume = 0L;
        long totalRevenue = 0L;
        ProductReportRowResp bestQty = null;
        ProductReportRowResp bestRev = rows.isEmpty() ? null : rows.get(0);

        for (ProductReportRowResp row : rows) {
            long qty = row.getTotalQuantity();
            long rev = row.getTotalRevenue();
            totalVolume += qty;
            totalRevenue += rev;
            if (bestQty == null || qty > bestQty.getTotalQuantity()) {
                bestQty = row;
            }
        }

        final long finalTotalRevenue = totalRevenue;
        rows.forEach(row -> {
            double percent = finalTotalRevenue > 0 ? (row.getTotalRevenue() * 100.0 / finalTotalRevenue) : 0D;
            double avgPrice = row.getTotalQuantity() > 0
                    ? (double) row.getTotalRevenue() / row.getTotalQuantity()
                    : 0D;
            row.setPercent(percent);
            row.setAvgPrice(avgPrice);
        });

        List<ProductReportRowResp> sorted = sortRowsIfNeeded(rows, sortBy);

        ProductReportSummaryResp summary = ProductReportSummaryResp.builder()
                .totalVolume(totalVolume)
                .totalRevenue(totalRevenue)
                .bestSellerQty(bestQty != null ? bestQty.getProductName() : "N/A")
                .bestSellerRev(bestRev != null ? bestRev.getProductName() : "N/A")
                .build();

        return ProductReportDataResp.builder()
                .summary(summary)
                .details(sorted)
                .build();
    }

    private List<ProductReportRowResp> sortRowsIfNeeded(List<ProductReportRowResp> rows, String sortBy) {
        switch (sortBy) {
            case "asc" -> rows.sort(Comparator.comparingLong(ProductReportRowResp::getTotalRevenue));
            case "top_5_high" -> {
                rows.sort(Comparator.comparingLong(ProductReportRowResp::getTotalRevenue).reversed());
                return rows.stream().limit(5).toList();
            }
            case "top_5_low" -> {
                rows.sort(Comparator.comparingLong(ProductReportRowResp::getTotalRevenue));
                return rows.stream().limit(5).toList();
            }
            default -> {
                // desc: keep DB order (already DESC), no extra sort
            }
        }
        return rows;
    }

    @Override
    public ResponseData getTotalRevenue(LocalDate startDate, LocalDate endDate, Long categoryId) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            Long revenue = productReportRepository.getTotalRevenue(startDateTime, endDateTime, categoryId);
            
            return ResponseData.builder()
                    .success(true)
                    .data(java.util.Map.of(
                            "totalRevenue", revenue,
                            "currency", "VND",
                            "period", startDate + " to " + endDate
                    ))
                    .build();
        } catch (Exception e) {
            return ResponseData.builder()
                    .success(false)
                    .message("Error calculating revenue: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseData getInventoryExpense(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            
            Long expense = productReportRepository.getInventoryExpense(startDateTime, endDateTime);
            
            return ResponseData.builder()
                    .success(true)
                    .data(java.util.Map.of(
                            "inventoryExpense", expense,
                            "currency", "VND",
                            "period", startDate + " to " + endDate
                    ))
                    .build();
        } catch (Exception e) {
            return ResponseData.builder()
                    .success(false)
                    .message("Error calculating inventory expense: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseData getSalaryExpense() {
        try {
            Long totalSalary = productReportRepository.getSalaryExpense();
            
            return ResponseData.builder()
                    .success(true)
                    .data(java.util.Map.of(
                            "totalSalaryExpense", totalSalary,
                            "currency", "VND"
                    ))
                    .build();
        } catch (Exception e) {
            return ResponseData.builder()
                    .success(false)
                    .message("Error calculating salary expense: " + e.getMessage())
                    .build();
        }
    }
}
