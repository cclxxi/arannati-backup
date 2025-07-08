package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @RestResource(path = "by-product-list", rel = "by-product-list")
    List<Review> findByProductIdAndActiveIsTrue(Long productId);

    @RestResource(path = "by-product-page", rel = "by-product-page")
    Page<Review> findByProductIdAndActiveIsTrue(Long productId, Pageable pageable);

    @RestResource(path = "by-user-list", rel = "by-user-list")
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @RestResource(path = "by-user-page", rel = "by-user-page")
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    Long getReviewCountByProductId(@Param("productId") Long productId);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}