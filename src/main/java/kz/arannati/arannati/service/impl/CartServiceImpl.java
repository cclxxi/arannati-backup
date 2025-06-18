package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Cart;
import kz.arannati.arannati.repository.CartRepository;
import kz.arannati.arannati.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public List<Cart> findByUserIdOrderByCreatedAtAsc(Long userId) {
        return null;
    }

    @Override
    public Optional<Cart> findByUserIdAndProductId(Long userId, Long productId) {
        return null;
    }

    @Override
    public List<Cart> findActiveCartItemsByUserId(Long userId) {
        return null;
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        
    }

    @Override
    public long countByUserId(Long userId) {
        return 0;
    }

    @Override
    public Integer getTotalQuantityByUserId(Long userId) {
        return null;
    }

    @Override
    public Cart save(Cart cart) {
        return null;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<Cart> findAll() {
        return null;
    }
}