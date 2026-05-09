package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.ProductReportFilterReq;

import com.example.COFFEEHOUSE.DTO.ResponseData;

import java.time.LocalDate;

public interface ProductReportService {
    ResponseData getReportData(ProductReportFilterReq filterReq);
    ResponseData getCategories();
    ResponseData getTotalRevenue(LocalDate startDate, LocalDate endDate);
    ResponseData getInventoryExpense(LocalDate startDate, LocalDate endDate);
    ResponseData getSalaryExpense(LocalDate startDate, LocalDate endDate);
    ResponseData getCompletedOrders(LocalDate startDate, LocalDate endDate);
}
