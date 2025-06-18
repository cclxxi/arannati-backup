package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findByIdAndActiveIsTrue(Long id);

    Optional<Product> findBySkuAndActiveIsTrue(String sku);

    Page<Product> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long categoryId, Pageable pageable);

    Page<Product> findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long brandId, Pageable pageable);

    Page<Product> findRegularProducts(Pageable pageable);

    Page<Product> findProfessionalProducts(Pageable pageable);

    Page<Product> searchProducts(String search, Pageable pageable);

    Page<Product> findProductsWithFilters(
            Long categoryId,
            Long brandId,
            Boolean isProfessional,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

    Page<Product> findNewProducts(Pageable pageable);

    Page<Product> findProductsOnSale(Pageable pageable);

    List<Product> findTop8ByActiveIsTrueOrderByCreatedAtDesc();

    List<Product> findSimilarProducts(Long categoryId, Long excludeId, int limit);

    boolean existsBySku(String sku);

    long countByActiveIsTrue();

    long countByCategoryIdAndActiveIsTrue(Long categoryId);

    long countByBrandIdAndActiveIsTrue(Long brandId);
    
    Product save(Product product);
    
    Optional<Product> findById(Long id);
    
    void deleteById(Long id);
    
    List<Product> findAll();
}