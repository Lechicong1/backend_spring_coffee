package com.example.COFFEEHOUSE.Service;

import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Request.CartItemUpdateQuantityReq;
import com.example.COFFEEHOUSE.DTO.Response.CartItemResp;

import java.util.List;

public interface CartItemService {
    void addToCart(CartItemReq request);

    void updateCartItem(Long id, CartItemUpdateQuantityReq request);

    void removeFromCart(Long id);

    void clearCart();

    List<CartItemResp> getCart();

    CartItemResp getCartItem(Long id);

    Long getCartCount(Long userId);

    void checkout(Long userId, boolean isBuyNow);

}

