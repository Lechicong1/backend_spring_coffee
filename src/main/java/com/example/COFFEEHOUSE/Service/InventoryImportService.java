package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.InventoryImportReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryImportResp;

import java.util.List;

public interface InventoryImportService {
    void createImport(InventoryImportReq request);
    void updateImport(Long id, InventoryImportReq request);
    void deleteImport(Long id);
    List<InventoryImportResp> findAll();
    InventoryImportResp findById(Long id);
    List<InventoryImportResp> search(String keyword);
}
