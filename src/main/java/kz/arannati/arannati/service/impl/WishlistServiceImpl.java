package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.entity.Wishlist;
import kz.arannati.arannati.repository.WishlistRepository;
import kz.arannati.arannati.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public WishlistDTO convertToDto(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        WishlistDTO.WishlistDTOBuilder builder = WishlistDTO.builder()
                .id(wishlist.getId())
                .createdAt(wishlist.getCreatedAt());

        if (wishlist.getUser() != null) {
            builder.userId(wishlist.getUser().getId());
        }

        if (wishlist.getProduct() != null) {
            builder.productId(wishlist.getProduct().getId());
        }

        return builder.build();
    }

    @Override
    public Wishlist convertToEntity(WishlistDTO wishlistDTO) {
        if (wishlistDTO == null) {
            return null;
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setId(wishlistDTO.getId());
        wishlist.setCreatedAt(wishlistDTO.getCreatedAt());

        // User and Product relationships are typically handled separately
        // as they require fetching the actual entities from the database

        return wishlist;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistDTO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        Page<Wishlist> wishlistPage = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<WishlistDTO> wishlistDTOs = wishlistPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(wishlistDTOs, pageable, wishlistPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDTO> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> findByUserIdAndProductId(Long userId, Long productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDTO> findActiveWishlistItemsByUserId(Long userId) {
        return wishlistRepository.findActiveWishlistItemsByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findMostWishedProducts(Pageable pageable) {
        return wishlistRepository.findMostWishedProducts(pageable);
    }

    @Override
    public WishlistDTO save(WishlistDTO wishlistDTO) {
        Wishlist wishlist = convertToEntity(wishlistDTO);

        // Handle user relationship if userId is provided
        if (wishlistDTO.getUserId() != null) {
            User user = new User();
            user.setId(wishlistDTO.getUserId());
            wishlist.setUser(user);
        }

        // Handle product relationship if productId is provided
        if (wishlistDTO.getProductId() != null) {
            Product product = new Product();
            product.setId(wishlistDTO.getProductId());
            wishlist.setProduct(product);
        }

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return convertToDto(savedWishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> findById(Long id) {
        return wishlistRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        wishlistRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDTO> findAll() {
        return wishlistRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
