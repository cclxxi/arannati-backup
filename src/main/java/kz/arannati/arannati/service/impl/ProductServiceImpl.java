package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.entity.Brand;
import kz.arannati.arannati.entity.Category;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.repository.ProductRepository;
import kz.arannati.arannati.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductDTO convertToDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO.ProductDTOBuilder builder = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .sku(product.getSku())
                .regularPrice(product.getRegularPrice())
                .cosmetologistPrice(product.getCosmetologistPrice())
                .salePrice(product.getSalePrice())
                .isProfessional(product.isProfessional())
                .isActive(product.isActive())
                .stockQuantity(product.getStockQuantity())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .ingredients(product.getIngredients())
                .usageInstructions(product.getUsageInstructions())
                .sortOrder(product.getSortOrder())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            builder.categoryId(product.getCategory().getId());
        }

        if (product.getBrand() != null) {
            builder.brandId(product.getBrand().getId());
        }

        return builder.build();
    }

    @Override
    public Product convertToEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setShortDescription(productDTO.getShortDescription());
        product.setSku(productDTO.getSku());
        product.setRegularPrice(productDTO.getRegularPrice());
        product.setCosmetologistPrice(productDTO.getCosmetologistPrice());
        product.setSalePrice(productDTO.getSalePrice());
        product.setProfessional(productDTO.isProfessional());
        product.setActive(productDTO.isActive());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setWeight(productDTO.getWeight());
        product.setDimensions(productDTO.getDimensions());
        product.setIngredients(productDTO.getIngredients());
        product.setUsageInstructions(productDTO.getUsageInstructions());
        product.setSortOrder(productDTO.getSortOrder());
        product.setCreatedAt(productDTO.getCreatedAt());
        product.setUpdatedAt(productDTO.getUpdatedAt());

        // Category and Brand relationships are typically handled separately
        // as they require fetching the actual entities from the database

        return product;
    }

    @Override
    public Optional<ProductDTO> findByIdAndActiveIsTrue(Long id) {
        return productRepository.findByIdAndActiveIsTrue(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ProductDTO> findBySkuAndActiveIsTrue(String sku) {
        return productRepository.findBySkuAndActiveIsTrue(sku)
                .map(this::convertToDto);
    }

    @Override
    public Page<ProductDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable) {
        Page<Product> productPage = productRepository.findByActiveIsTrueOrderBySortOrderAscNameAsc(pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(categoryId, pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long brandId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(brandId, pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findRegularProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findRegularProducts(pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findProfessionalProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findProfessionalProducts(pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> searchProducts(String search, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(search, pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findProductsWithFilters(Long categoryId, Long brandId, Boolean isProfessional, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> productPage = productRepository.findProductsWithFilters(categoryId, brandId, isProfessional, minPrice, maxPrice, pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findNewProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findNewProducts(pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> findProductsOnSale(Pageable pageable) {
        Page<Product> productPage = productRepository.findProductsOnSale(pageable);
        List<ProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductDTO> findTop8ByActiveIsTrueOrderByCreatedAtDesc() {
        return productRepository.findTop8ByActiveIsTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> findSimilarProducts(Long categoryId, Long excludeId, int limit) {
        return productRepository.findSimilarProducts(categoryId, excludeId, limit)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    public long countByActiveIsTrue() {
        return productRepository.countByActiveIsTrue();
    }

    @Override
    public long countByCategoryIdAndActiveIsTrue(Long categoryId) {
        return productRepository.countByCategoryIdAndActiveIsTrue(categoryId);
    }

    @Override
    public long countByBrandIdAndActiveIsTrue(Long brandId) {
        return productRepository.countByBrandIdAndActiveIsTrue(brandId);
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);

        // Handle category relationship if categoryId is provided
        if (productDTO.getCategoryId() != null) {
            // This would typically involve fetching the Category entity from a repository
            // For simplicity, we're setting a reference with just the ID
            Category category = new Category();
            category.setId(productDTO.getCategoryId());
            product.setCategory(category);
        }

        // Handle brand relationship if brandId is provided
        if (productDTO.getBrandId() != null) {
            // This would typically involve fetching the Brand entity from a repository
            // For simplicity, we're setting a reference with just the ID
            Brand brand = new Brand();
            brand.setId(productDTO.getBrandId());
            product.setBrand(brand);
        }

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
