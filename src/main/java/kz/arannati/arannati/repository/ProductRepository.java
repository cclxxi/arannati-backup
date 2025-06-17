package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActiveIsTrue(Long id);

    Optional<Product> findBySkuAndActiveIsTrue(String sku);

    Page<Product> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long categoryId, Pageable pageable);

    Page<Product> findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long brandId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isProfessional = false " +
            "ORDER BY p.sortOrder ASC, p.name ASC")
    Page<Product> findRegularProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isProfessional = true " +
            "ORDER BY p.sortOrder ASC, p.name ASC")
    Page<Product> findProfessionalProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
            "(:isProfessional IS NULL OR p.isProfessional = :isProfessional) AND " +
            "(:minPrice IS NULL OR p.regularPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.regularPrice <= :maxPrice)")
    Page<Product> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("isProfessional") Boolean isProfessional,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity > 0 " +
            "ORDER BY p.createdAt DESC")
    Page<Product> findNewProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.salePrice IS NOT NULL " +
            "ORDER BY ((p.regularPrice - p.salePrice) / p.regularPrice) DESC")
    Page<Product> findProductsOnSale(Pageable pageable);

    List<Product> findTop8ByActiveIsTrueOrderByCreatedAtDesc();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true AND p.id != :excludeId")
    List<Product> findSimilarProductsBase(@Param("categoryId") Long categoryId,
                                         @Param("excludeId") Long excludeId);

    default List<Product> findSimilarProducts(Long categoryId, Long excludeId, int limit) {
        List<Product> products = findSimilarProductsBase(categoryId, excludeId);
        // Shuffle the list to simulate ORDER BY RAND()
        java.util.Collections.shuffle(products);
        // Return only up to limit elements
        return products.stream().limit(limit).toList();
    }

    boolean existsBySku(String sku);

    long countByActiveIsTrue();

    long countByCategoryIdAndActiveIsTrue(Long categoryId);

    long countByBrandIdAndActiveIsTrue(Long brandId);
}
