package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<ResponseData> addToCart(@RequestBody CartItemReq request) {
        cartItemService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Item added to cart successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateCartItem(@PathVariable Long id, @RequestBody CartItemReq request) {
        cartItemService.updateCartItem(id, request);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart item updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData> removeFromCart(@PathVariable Long id) {
        cartItemService.removeFromCart(id);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Item removed from cart successfully")
                .build());
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseData> clearCart(@PathVariable Long userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart cleared successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getCartItem(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart item retrieved successfully")
                .data(cartItemService.getCartItem(id))
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseData> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart retrieved successfully")
                .data(cartItemService.getCart(userId))
                .build());
    }
}

