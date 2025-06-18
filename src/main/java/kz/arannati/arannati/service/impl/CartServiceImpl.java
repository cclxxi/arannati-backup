package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.CartDTO;
import kz.arannati.arannati.entity.Cart;
import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.CartRepository;
import kz.arannati.arannati.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public CartDTO convertToDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .productId(cart.getProduct().getId())
                .quantity(cart.getQuantity())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    @Override
    public Cart convertToEntity(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setId(cartDTO.getId());
        cart.setQuantity(cartDTO.getQuantity());
        cart.setCreatedAt(cartDTO.getCreatedAt());
        cart.setUpdatedAt(cartDTO.getUpdatedAt());

        // Handle user relationship if userId is provided
        if (cartDTO.getUserId() != null) {
            User user = new User();
            user.setId(cartDTO.getUserId());
            cart.setUser(user);
        }

        // Handle product relationship if productId is provided
        if (cartDTO.getProductId() != null) {
            Product product = new Product();
            product.setId(cartDTO.getProductId());
            cart.setProduct(product);
        }

        return cart;
    }

    @Override
    public List<CartDTO> findByUserIdOrderByCreatedAtAsc(Long userId) {
        return cartRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CartDTO> findByUserIdAndProductId(Long userId, Long productId) {
        return cartRepository.findByUserIdAndProductId(userId, productId)
                .map(this::convertToDto);
    }

    @Override
    public List<CartDTO> findActiveCartItemsByUserId(Long userId) {
        return cartRepository.findActiveCartItemsByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public long countByUserId(Long userId) {
        return cartRepository.countByUserId(userId);
    }

    @Override
    public Integer getTotalQuantityByUserId(Long userId) {
        return cartRepository.getTotalQuantityByUserId(userId);
    }

    @Override
    public CartDTO save(CartDTO cartDTO) {
        Cart cart = convertToEntity(cartDTO);
        Cart savedCart = cartRepository.save(cart);
        return convertToDto(savedCart);
    }

    @Override
    public Optional<CartDTO> findById(Long id) {
        return cartRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public List<CartDTO> findAll() {
        return cartRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
