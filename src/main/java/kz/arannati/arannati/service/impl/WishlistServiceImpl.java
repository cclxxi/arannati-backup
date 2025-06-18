package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Wishlist;
import kz.arannati.arannati.repository.WishlistRepository;
import kz.arannati.arannati.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Page<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return null;
    }

    @Override
    public Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId) {
        return null;
    }

    @Override
    public List<Wishlist> findActiveWishlistItemsByUserId(Long userId) {
        return null;
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return false;
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        
    }

    @Override
    public long countByUserId(Long userId) {
        return 0;
    }

    @Override
    public List<Object[]> findMostWishedProducts(Pageable pageable) {
        return null;
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        return null;
    }

    @Override
    public Optional<Wishlist> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<Wishlist> findAll() {
        return null;
    }
}