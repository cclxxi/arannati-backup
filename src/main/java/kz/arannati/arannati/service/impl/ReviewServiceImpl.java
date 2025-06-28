package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.ReviewDTO;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.entity.Review;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.ProductRepository;
import kz.arannati.arannati.repository.ReviewRepository;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public ReviewDTO save(ReviewDTO reviewDTO) {
        Review review = convertToEntity(reviewDTO);
        review = reviewRepository.save(review);
        return convertToDto(review);
    }

    @Override
    public Optional<ReviewDTO> findById(Long id) {
        return reviewRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<ReviewDTO> findByProductIdAndActiveIsTrue(Long productId) {
        return reviewRepository.findByProductIdAndIsActiveIsTrue(productId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewDTO> findByProductIdAndActiveIsTrue(Long productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByProductIdAndIsActiveIsTrue(productId, pageable);
        List<ReviewDTO> reviewDTOs = reviewPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewDTOs, pageable, reviewPage.getTotalElements());
    }

    @Override
    public List<ReviewDTO> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<ReviewDTO> reviewDTOs = reviewPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewDTOs, pageable, reviewPage.getTotalElements());
    }

    @Override
    public Page<ReviewDTO> findAllWithPagination(Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        List<ReviewDTO> reviewDTOs = reviewPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(reviewDTOs, pageable, reviewPage.getTotalElements());
    }

    @Override
    public Double getAverageRatingByProductId(Long productId) {
        return reviewRepository.getAverageRatingByProductId(productId);
    }

    @Override
    public Long getReviewCountByProductId(Long productId) {
        return reviewRepository.getReviewCountByProductId(productId);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public boolean canUserReviewProduct(Long userId, Long productId) {
        // Check if user has already reviewed this product
        return !reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public ReviewDTO convertToDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO.ReviewDTOBuilder builder = ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .verifiedPurchase(review.isVerifiedPurchase())
                .active(review.isActive())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt());

        if (review.getProduct() != null) {
            builder.productId(review.getProduct().getId());
        }

        if (review.getUser() != null) {
            builder.userId(review.getUser().getId());
            builder.userFirstName(review.getUser().getFirstName());
            builder.userLastName(review.getUser().getLastName());
        }

        return builder.build();
    }

    @Override
    public Review convertToEntity(ReviewDTO reviewDTO) {
        if (reviewDTO == null) {
            return null;
        }

        Review review = new Review();
        review.setId(reviewDTO.getId());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setVerifiedPurchase(reviewDTO.isVerifiedPurchase());
        review.setActive(reviewDTO.isActive());

        if (reviewDTO.getCreatedAt() != null) {
            review.setCreatedAt(reviewDTO.getCreatedAt());
        } else {
            review.setCreatedAt(LocalDateTime.now());
        }

        if (reviewDTO.getUpdatedAt() != null) {
            review.setUpdatedAt(reviewDTO.getUpdatedAt());
        } else {
            review.setUpdatedAt(LocalDateTime.now());
        }

        if (reviewDTO.getProductId() != null) {
            Product product = productRepository.findById(reviewDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + reviewDTO.getProductId()));
            review.setProduct(product);
        }

        if (reviewDTO.getUserId() != null) {
            User user = userRepository.findById(reviewDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + reviewDTO.getUserId()));
            review.setUser(user);
        }

        return review;
    }
}
