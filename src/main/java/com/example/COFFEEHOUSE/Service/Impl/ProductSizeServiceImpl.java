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
        if (sizes == null) {
            return;
        }

        List<ProductSizeEntity> existingSizes = productSizeRepo.findByProductId(productId);

        // Update or create sizes
        for (ProductSizeReq req : sizes) {
            ProductSizeEntity existingSize = existingSizes.stream()
                    .filter(existing -> existing.getSizeName().equalsIgnoreCase(req.getSizeName()))
                    .findFirst()
                    .orElse(null);

            if (existingSize != null) {
                // Update price
                existingSize.setPrice(req.getPrice() != null ? req.getPrice() : 0L);
                productSizeRepo.save(existingSize);
            } else {
                // Create new
                ProductSizeEntity newSize = productSizeMapper.toEntity(req);
                newSize.setProductId(productId);
                newSize.setPrice(req.getPrice() != null ? req.getPrice() : 0L);
                productSizeRepo.save(newSize);
            }
        }
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

