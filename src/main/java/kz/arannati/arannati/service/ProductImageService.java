package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageService {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    Optional<ProductImage> findByProductIdAndMainIsTrue(Long productId);

    Optional<ProductImage> findFirstImageForProduct(Long productId);

    void clearMainImageForProduct(Long productId);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);
    
    ProductImage save(ProductImage productImage);
    
    Optional<ProductImage> findById(Long id);
    
    void deleteById(Long id);
    
    List<ProductImage> findAll();
}