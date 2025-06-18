package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ProductImageDTO;
import kz.arannati.arannati.entity.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageService {

    List<ProductImageDTO> findByProductIdOrderBySortOrderAsc(Long productId);

    Optional<ProductImageDTO> findByProductIdAndMainIsTrue(Long productId);

    Optional<ProductImageDTO> findFirstImageForProduct(Long productId);

    void clearMainImageForProduct(Long productId);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);

    ProductImageDTO save(ProductImageDTO productImageDTO);

    Optional<ProductImageDTO> findById(Long id);

    void deleteById(Long id);

    List<ProductImageDTO> findAll();

    ProductImageDTO convertToDto(ProductImage productImage);

    ProductImage convertToEntity(ProductImageDTO productImageDTO);
}
