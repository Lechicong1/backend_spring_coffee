package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Mapper.ProductMapper;
import com.example.COFFEEHOUSE.DTO.Request.ProductReq;
import com.example.COFFEEHOUSE.DTO.Response.ProductResp;
import com.example.COFFEEHOUSE.Entity.ProductEntity;
import com.example.COFFEEHOUSE.Reposistory.ProductRepo;
import com.example.COFFEEHOUSE.Service.ProductService;
import com.example.COFFEEHOUSE.Service.ProductSizeService;
import com.example.COFFEEHOUSE.Utils.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ProductSizeService productSizeService;
    private final ProductMapper productMapper;
    private final FileStorage fileStorage;

    @Override
    @Transactional
    public void createProduct(ProductReq request, MultipartFile image) {
        ProductEntity entity = productMapper.toEntity(request);
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorage.saveFile(image, "products");
            entity.setImageUrl(imageUrl);
        }
        ProductEntity savedEntity = productRepo.save(entity);

        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            productSizeService.saveSizesForProduct(savedEntity.getId(), request.getSizes());
        }
    }

    @Override
    @Transactional
    public void updateProduct(Long id, ProductReq request, MultipartFile image) {
        ProductEntity entity = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productMapper.updateEntityFromRequest(request, entity);
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorage.saveFile(image, "products");
            entity.setImageUrl(imageUrl);
        }
        productRepo.save(entity);

        if (request.getSizes() != null) {
            productSizeService.updateSizesForProduct(id, request.getSizes());
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity entity = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productSizeService.deleteSizesByProductId(id);
        productRepo.delete(entity);
    }

    @Override
    public List<ProductResp> findAll() {
        return productRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResp findById(Long id) {
        ProductEntity entity = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(entity);
    }

    private ProductResp mapToResponse(ProductEntity entity) {
        ProductResp resp = productMapper.toDTO(entity);
        resp.setSizes(productSizeService.getSizesByProductId(entity.getId()));
        return resp;
    }
}

