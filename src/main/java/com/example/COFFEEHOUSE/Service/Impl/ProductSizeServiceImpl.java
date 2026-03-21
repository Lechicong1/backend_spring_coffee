package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.ProductSizeMapper;
import com.example.COFFEEHOUSE.DTO.Request.ProductSizeReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductSizeResp;
import com.example.COFFEEHOUSE.Entity.ProductSizeEntity;
import com.example.COFFEEHOUSE.Reposistory.ProductSizeRepo;
import com.example.COFFEEHOUSE.Service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepo productSizeRepo;
    private final ProductSizeMapper productSizeMapper;

    @Override
    @Transactional
    public void saveSizesForProduct(Long productId, List<ProductSizeReq> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return;
        }
        List<ProductSizeEntity> sizeEntities = sizes.stream()
                .map(req -> {
                    ProductSizeEntity entity = productSizeMapper.toEntity(req);
                    entity.setProductId(productId);
                    if (entity.getPrice() == null) {
                        entity.setPrice(0L);
                    }
                    return entity;
                })
                .collect(Collectors.toList());
        productSizeRepo.saveAll(sizeEntities);
    }

    @Override
    @Transactional
    public void updateSizesForProduct(Long productId, List<ProductSizeReq> sizes) {
        // Simple approach: delete existing and insert new
        productSizeRepo.deleteByProductId(productId);
        saveSizesForProduct(productId, sizes);
    }

    @Override
    public List<ProductSizeResp> getSizesByProductId(Long productId) {
        List<ProductSizeEntity> entities = productSizeRepo.findByProductId(productId);
        return productSizeMapper.toDTOList(entities);
    }

    @Override
    @Transactional
    public void deleteSizesByProductId(Long productId) {
        productSizeRepo.deleteByProductId(productId);
    }
}

