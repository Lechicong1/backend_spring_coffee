package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.IngredientReq;
import com.example.COFFEEHOUSE.DTO.Request.OrderItemReq;
import com.example.COFFEEHOUSE.DTO.Response.IngredientResp;

import java.util.List;

public interface IngredientService {
    void createIngredient(IngredientReq request);

    void updateIngredient(Long id, IngredientReq request);

    void deleteIngredient(Long id);

    List<IngredientResp> findAll();

    IngredientResp findById(Long id);

    List<IngredientResp> search(String keyword);
    void deductIngredients(List<OrderItemReq> items);
    void refundIngredients(List<OrderItemReq> items);
}
