package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.InventoryCheckReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryCheckResp;

import java.time.LocalDate;
import java.util.List;

public interface InventoryCheckService {

    void createCheck(InventoryCheckReq request);
    void updateCheck(InventoryCheckReq request);
    void deleteCheck(Long id);
    List<InventoryCheckResp> findAll();
    InventoryCheckResp findById(Long id);
    List<InventoryCheckResp> findByDate(LocalDate date);
    List<InventoryCheckResp> search(String keyword);
    List<InventoryCheckResp> getInventoryCheckDataByDate(LocalDate date);
}
