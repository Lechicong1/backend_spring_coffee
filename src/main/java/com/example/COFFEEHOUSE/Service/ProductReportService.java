package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.ProductReportFilterReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductReportDataResp;
import com.example.COFFEEHOUSE.DTO.ResponseData;

public interface ProductReportService {
    ResponseData getReportData(ProductReportFilterReq filterReq);
    ResponseData getCategories();
}

