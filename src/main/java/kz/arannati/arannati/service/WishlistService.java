package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WishlistService {

    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findActiveWishlistItemsByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    List<Object[]> findMostWishedProducts(Pageable pageable);
    
    Wishlist save(Wishlist wishlist);
    
    Optional<Wishlist> findById(Long id);
    
    void deleteById(Long id);
    
    List<Wishlist> findAll();
}