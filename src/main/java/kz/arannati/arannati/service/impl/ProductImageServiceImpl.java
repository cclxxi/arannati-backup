package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.ProductImage;
import kz.arannati.arannati.repository.ProductImageRepository;
import kz.arannati.arannati.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @Override
    public List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId) {
        return null;
    }

    @Override
    public Optional<ProductImage> findByProductIdAndMainIsTrue(Long productId) {
        return null;
    }

    @Override
    public Optional<ProductImage> findFirstImageForProduct(Long productId) {
        return null;
    }

    @Override
    public void clearMainImageForProduct(Long productId) {
        
    }

    @Override
    public void deleteByProductId(Long productId) {
        
    }

    @Override
    public long countByProductId(Long productId) {
        return 0;
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        return null;
    }

    @Override
    public Optional<ProductImage> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<ProductImage> findAll() {
        return null;
    }
}