package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.CartItemMapper;
import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Response.CartItemResp;
import com.example.COFFEEHOUSE.Entity.CartItemEntity;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.CartItemRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImp implements CartItemService {
    private final CartItemMapper cartItemMapper;
    private final CartItemRepo cartItemRepo;
    private final UserRepo userRepo;

    @Override
    public void addToCart(CartItemReq request) {
        validateCartItemRequest(request);

        if (!userRepo.existsById(request.getUserId())) {
            throw new ResourceNotFoundException("User not found with id: " + request.getUserId());
        }

        var existingItem = cartItemRepo.findByUserIdAndProductSizeId(request.getUserId(), request.getProductSizeId());

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepo.save(item);
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .userId(request.getUserId())
                    .productSizeId(request.getProductSizeId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepo.save(newItem);
        }
    }

    @Override
    public void updateCartItem(Long id, CartItemReq request) {
        CartItemEntity existing = cartItemRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than 0");
        }

        existing.setQuantity(request.getQuantity());
        cartItemRepo.save(existing);
    }

    @Override
    public void removeFromCart(Long id) {
        if (!cartItemRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cart item not found with id: " + id);
        }
        cartItemRepo.deleteById(id);
    }

    @Override
    public void clearCart(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        cartItemRepo.deleteByUserId(userId);
    }

    @Override
    public List<CartItemResp> getCart(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return cartItemMapper.toDTOList(cartItemRepo.findByUserId(userId));
    }

    @Override
    public CartItemResp getCartItem(Long id) {
        return cartItemRepo.findById(id)
                .map(cartItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));
    }

    private void validateCartItemRequest(CartItemReq request) {
        if (request.getUserId() == null) {
            throw new InvalidInputException("User ID is required");
        }
        if (request.getProductSizeId() == null) {
            throw new InvalidInputException("Product Size ID is required");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than 0");
        }
    }
}

