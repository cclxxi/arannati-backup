package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.CatalogFilterDTO;
import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<ProductDTO> findByIdAndActiveIsTrue(Long id);

    Optional<ProductDTO> findBySkuAndActiveIsTrue(String sku);

    Page<ProductDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Page<ProductDTO> findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long categoryId, Pageable pageable);

    Page<ProductDTO> findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long brandId, Pageable pageable);

    Page<ProductDTO> findRegularProducts(Pageable pageable);

    Page<ProductDTO> findProfessionalProducts(Pageable pageable);

    Page<ProductDTO> searchProducts(String search, Pageable pageable);

    Page<ProductDTO> findProductsWithFilters(
            Long categoryId,
            Long brandId,
            Boolean isProfessional,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

    Page<ProductDTO> findNewProducts(Pageable pageable);

    Page<ProductDTO> findProductsOnSale(Pageable pageable);

    long countProductsOnSale();

    List<ProductDTO> findAllActiveProducts();

    long countLowStockProducts(int threshold);

    Page<ProductDTO> findAllWithPagination(Pageable pageable);

    List<ProductDTO> findByBrandIdAndActiveIsTrue(Long brandId);

    List<ProductDTO> findProductsOnSale();

    List<ProductDTO> findProductsWithCustomFilters(CatalogFilterDTO filter);

    List<ProductDTO> findTop8ByActiveIsTrueOrderByCreatedAtDesc();

    List<ProductDTO> findSimilarProducts(Long categoryId, Long excludeId, int limit);

    boolean existsBySku(String sku);

    long countByActiveIsTrue();

    long countByCategoryIdAndActiveIsTrue(Long categoryId);

    long countByBrandIdAndActiveIsTrue(Long brandId);

    ProductDTO save(ProductDTO productDTO);

    Optional<ProductDTO> findById(Long id);

    void deleteById(Long id);

    List<ProductDTO> findAll();

    ProductDTO convertToDto(Product product);

    Product convertToEntity(ProductDTO productDTO);
}
