package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.ProductSizeReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductSizeResp;

import java.util.List;

public interface ProductSizeService {
    void saveSizesForProduct(Long productId, List<ProductSizeReq> sizes);
    void updateSizesForProduct(Long productId, List<ProductSizeReq> sizes);
    List<ProductSizeResp> getSizesByProductId(Long productId);
    void deleteSizesByProductId(Long productId);
}
