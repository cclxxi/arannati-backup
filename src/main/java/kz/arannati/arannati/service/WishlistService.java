package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WishlistService {

    Page<WishlistDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<WishlistDTO> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<WishlistDTO> findByUserIdAndProductId(Long userId, Long productId);

    List<WishlistDTO> findActiveWishlistItemsByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    List<Object[]> findMostWishedProducts(Pageable pageable);

    WishlistDTO save(WishlistDTO wishlistDTO);

    Optional<WishlistDTO> findById(Long id);

    void deleteById(Long id);

    List<WishlistDTO> findAll();

    WishlistDTO convertToDto(Wishlist wishlist);

    Wishlist convertToEntity(WishlistDTO wishlistDTO);
}
