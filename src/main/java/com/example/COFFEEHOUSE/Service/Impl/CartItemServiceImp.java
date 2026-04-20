package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.CartItemMapper;
import com.example.COFFEEHOUSE.DTO.Request.CartItemReq;
import com.example.COFFEEHOUSE.DTO.Request.CartItemUpdateQuantityReq;
import com.example.COFFEEHOUSE.DTO.Response.CartItemResp;
import com.example.COFFEEHOUSE.Entity.CartItemEntity;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.CartItemRepo;
import com.example.COFFEEHOUSE.Reposistory.UserRepo;
import com.example.COFFEEHOUSE.Service.CartItemService;
import com.example.COFFEEHOUSE.Utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImp implements CartItemService {
    private final CartItemMapper cartItemMapper;
    private final CartItemRepo cartItemRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public void addToCart(CartItemReq request) {
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

    // Cập nhật số lượng của một mục trong giỏ hàng
    @Override
    @Transactional
    public void updateCartItem(Long id, CartItemUpdateQuantityReq request) {
        CartItemEntity existing = cartItemRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));

        if (request.getQuantity() <= 0) {
            cartItemRepo.delete(existing);
            return;
        }

        existing.setQuantity(request.getQuantity());
        cartItemRepo.save(existing);
    }

    // Xóa một mục khỏi giỏ hàng
    @Override
    @Transactional
    public void removeFromCart(Long id) {
        if (!cartItemRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cart item not found with id: " + id);
        }
        cartItemRepo.deleteById(id);
    }

    @Override
    public void clearCart() {
        Long userId = CommonUtils.getIdUserFromToken();
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        cartItemRepo.deleteByUserId(userId);
    }

    @Override
    public List<CartItemResp> getCart() {
        Long userId = CommonUtils.getIdUserFromToken();
        if (!userRepo.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return cartItemMapper.toDTOList(cartItemRepo.findByUserId(userId));
    }

    // Lấy thông tin chi tiết của một mục trong giỏ hàng
    @Override
    public CartItemResp getCartItem(Long id) {
        return cartItemRepo.findById(id)
                .map(cartItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));
    }



}
