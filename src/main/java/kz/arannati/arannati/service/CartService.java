package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {

    List<Cart> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    List<Cart> findActiveCartItemsByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    Integer getTotalQuantityByUserId(Long userId);
    
    Cart save(Cart cart);
    
    Optional<Cart> findById(Long id);
    
    void deleteById(Long id);
    
    List<Cart> findAll();
}