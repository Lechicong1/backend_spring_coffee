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

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseData> getCartCount(@PathVariable Long userId) {
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message("Cart count retrieved successfully")
                .data(cartItemService.getCartCount(userId))
                .build());
    }

    @PostMapping("/checkout")
    public ResponseEntity<ResponseData> checkout(@RequestParam Long userId, @RequestParam(defaultValue = "false") boolean isBuyNow) {
        cartItemService.checkout(userId, isBuyNow);
        return ResponseEntity.ok(ResponseData.builder()
                .success(true)
                .message(isBuyNow ? "Checkout completed (Buy Now - cart preserved)" : "Checkout completed and cart cleared")
                .build());
    }
}

