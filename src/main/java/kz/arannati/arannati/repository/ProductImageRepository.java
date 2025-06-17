package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    Optional<ProductImage> findByProductIdAndMainIsTrue(Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.sortOrder ASC")
    Optional<ProductImage> findFirstImageForProduct(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.main = false WHERE pi.product.id = :productId")
    void clearMainImageForProduct(@Param("productId") Long productId);

    void deleteByProductId(Long productId);

    long countByProductId(Long productId);
}
