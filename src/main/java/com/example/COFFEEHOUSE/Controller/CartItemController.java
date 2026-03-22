package com.example.COFFEEHOUSE.Controller;

import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Request.CartItemUpdateQuantityReq;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.CartItemService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ResponseData> addToCart(@Valid @RequestBody CartItemReq request) {
        cartItemService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.builder()
                        .success(true)
                        .message("Item added to cart successfully")
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseData> updateCartItem(@PathVariable Long id,@Valid @RequestBody CartItemUpdateQuantityReq request) {
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

    @DeleteMapping("/clearCart")
    public ResponseEntity<ResponseData> clearCart() {
        cartItemService.clearCart();
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

    @GetMapping("/myCart")
    public ResponseEntity<ResponseData> getCart() {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart retrieved successfully")
                .data(cartItemService.getCart())
                .build());
    }


}

