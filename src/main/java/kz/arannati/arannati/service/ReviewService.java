package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ReviewDTO;
import kz.arannati.arannati.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewDTO save(ReviewDTO reviewDTO);
    Optional<ReviewDTO> findById(Long id);
    List<ReviewDTO> findByProductIdAndActiveIsTrue(Long productId);
    Page<ReviewDTO> findByProductIdAndActiveIsTrue(Long productId, Pageable pageable);
    List<ReviewDTO> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<ReviewDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<ReviewDTO> findAllWithPagination(Pageable pageable);
    Double getAverageRatingByProductId(Long productId);
    Long getReviewCountByProductId(Long productId);
    void deleteById(Long id);
    boolean canUserReviewProduct(Long userId, Long productId);
    ReviewDTO convertToDto(Review review);
    Review convertToEntity(ReviewDTO reviewDTO);
}
