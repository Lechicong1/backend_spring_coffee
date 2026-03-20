package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Response.CartItemResp;

import java.util.List;

public interface CartItemService {
    void addToCart(CartItemReq request);

    void updateCartItem(Long id, CartItemReq request);

    void removeFromCart(Long id);

    void clearCart(Long userId);

    List<CartItemResp> getCart(Long userId);

    CartItemResp getCartItem(Long id);
}

