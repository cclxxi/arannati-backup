package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.CartDTO;
import kz.arannati.arannati.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {

    List<CartDTO> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<CartDTO> findByUserIdAndProductId(Long userId, Long productId);

    List<CartDTO> findActiveCartItemsByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    Integer getTotalQuantityByUserId(Long userId);

    CartDTO save(CartDTO cartDTO);

    Optional<CartDTO> findById(Long id);

    void deleteById(Long id);

    List<CartDTO> findAll();

    CartDTO convertToDto(Cart cart);

    Cart convertToEntity(CartDTO cartDTO);
}
