package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.ProductImageDTO;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.entity.ProductImage;
import kz.arannati.arannati.repository.ProductImageRepository;
import kz.arannati.arannati.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @Override
    public ProductImageDTO convertToDto(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }

        ProductImageDTO.ProductImageDTOBuilder builder = ProductImageDTO.builder()
                .id(productImage.getId())
                .imagePath(productImage.getImagePath())
                .altText(productImage.getAltText())
                .isMain(productImage.isMain())
                .sortOrder(productImage.getSortOrder())
                .createdAt(productImage.getCreatedAt());

        if (productImage.getProduct() != null) {
            builder.productId(productImage.getProduct().getId());
        }

        return builder.build();
    }

    @Override
    public ProductImage convertToEntity(ProductImageDTO productImageDTO) {
        if (productImageDTO == null) {
            return null;
        }

        ProductImage productImage = new ProductImage();
        productImage.setId(productImageDTO.getId());
        productImage.setImagePath(productImageDTO.getImagePath());
        productImage.setAltText(productImageDTO.getAltText());
        productImage.setMain(productImageDTO.isMain());
        productImage.setSortOrder(productImageDTO.getSortOrder());
        productImage.setCreatedAt(productImageDTO.getCreatedAt());

        // Product relationship is typically handled separately
        // as it requires fetching the actual entity from the database

        return productImage;
    }

    @Override
    public List<ProductImageDTO> findByProductIdOrderBySortOrderAsc(Long productId) {
        return productImageRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductImageDTO> findByProductIdAndMainIsTrue(Long productId) {
        return productImageRepository.findByProductIdAndMainIsTrue(productId)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ProductImageDTO> findFirstImageForProduct(Long productId) {
        return productImageRepository.findFirstImageForProduct(productId)
                .map(this::convertToDto);
    }

    @Override
    public void clearMainImageForProduct(Long productId) {
        productImageRepository.clearMainImageForProduct(productId);
    }

    @Override
    public void deleteByProductId(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }

    @Override
    public long countByProductId(Long productId) {
        return productImageRepository.countByProductId(productId);
    }

    @Override
    public ProductImageDTO save(ProductImageDTO productImageDTO) {
        ProductImage productImage = convertToEntity(productImageDTO);

        // Handle product relationship if productId is provided
        if (productImageDTO.getProductId() != null) {
            Product product = new Product();
            product.setId(productImageDTO.getProductId());
            productImage.setProduct(product);
        }

        ProductImage savedProductImage = productImageRepository.save(productImage);
        return convertToDto(savedProductImage);
    }

    @Override
    public Optional<ProductImageDTO> findById(Long id) {
        return productImageRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public List<ProductImageDTO> findAll() {
        return productImageRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
