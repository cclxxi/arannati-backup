package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final PricingService pricingService;

    /**
     * Cart page
     */
    @GetMapping("/cart")
    public String cartPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/auth/login";
        }

        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(user.getId());
        List<CartItemWithProductDTO> cartItemsWithProducts = cartItems.stream()
                .map(this::enrichCartItemWithProduct)
                .toList();

        BigDecimal totalAmount = calculateCartTotal(cartItemsWithProducts, user);

        model.addAttribute("cartItems", cartItemsWithProducts);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());

        return "cart/index";
    }

    /**
     * Add product to cart
     */
    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        UserDTO user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            // Check if product exists and is active
            Optional<ProductDTO> productOpt = productService.findById(productId);
            if (productOpt.isEmpty() || !productOpt.get().isActive()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Товар не найден"));
            }

            ProductDTO product = productOpt.get();

            // Check stock
            if (product.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", "Недостаточно товара на складе"));
            }

            // Check if item already in cart
            Optional<CartDTO> existingItem = cartService.findByUserIdAndProductId(user.getId(), productId);

            if (existingItem.isPresent()) {
                CartDTO cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + quantity;

                if (newQuantity > product.getStockQuantity()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Превышено количество товара на складе"));
                }

                cartItem.setQuantity(newQuantity);
                cartService.save(cartItem);
            } else {
                CartDTO newCartItem = CartDTO.builder()
                        .userId(user.getId())
                        .productId(productId)
                        .quantity(quantity)
                        .build();
                cartService.save(newCartItem);
            }

            // Get updated cart count
            Integer cartCount = cartService.getTotalQuantityByUserId(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Товар добавлен в корзину",
                    "cartCount", cartCount != null ? cartCount : 0
            ));

        } catch (Exception e) {
            log.error("Error adding product to cart", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка добавления товара в корзину"));
        }
    }

    /**
     * Update cart item quantity
     */
    @PostMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        UserDTO user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            if (quantity <= 0) {
                cartService.deleteByUserIdAndProductId(user.getId(), productId);
                return ResponseEntity.ok(Map.of("success", true, "message", "Товар удален из корзины"));
            }

            Optional<CartDTO> cartItemOpt = cartService.findByUserIdAndProductId(user.getId(), productId);
            if (cartItemOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Товар не найден в корзине"));
            }

            Optional<ProductDTO> productOpt = productService.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Товар не найден"));
            }

            ProductDTO product = productOpt.get();
            if (quantity > product.getStockQuantity()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Превышено количество товара на складе"));
            }

            CartDTO cartItem = cartItemOpt.get();
            cartItem.setQuantity(quantity);
            cartService.save(cartItem);

            // Calculate new item total
            BigDecimal price = pricingService.getEffectivePrice(product, user);
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Количество обновлено",
                    "itemTotal", itemTotal
            ));

        } catch (Exception e) {
            log.error("Error updating cart item", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления корзины"));
        }
    }

    /**
     * Remove item from cart
     */
    @PostMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam Long productId) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            cartService.deleteByUserIdAndProductId(user.getId(), productId);
            Integer cartCount = cartService.getTotalQuantityByUserId(user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Товар удален из корзины",
                    "cartCount", cartCount != null ? cartCount : 0
            ));

        } catch (Exception e) {
            log.error("Error removing item from cart", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка удаления товара"));
        }
    }

    /**
     * Clear entire cart
     */
    @PostMapping("/api/cart/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart() {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            cartService.deleteAllByUserId(user.getId());
            return ResponseEntity.ok(Map.of("success", true, "message", "Корзина очищена"));
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка очистки корзины"));
        }
    }

    private UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByEmail(auth.getName()).orElse(null);
    }

    private CartItemWithProductDTO enrichCartItemWithProduct(CartDTO cartItem) {
        Optional<ProductDTO> productOpt = productService.findById(cartItem.getProductId());
        if (productOpt.isEmpty()) {
            return null;
        }

        ProductDTO product = productOpt.get();
        UserDTO user = getCurrentUser();
        BigDecimal effectivePrice = pricingService.getEffectivePrice(product, user);

        return CartItemWithProductDTO.builder()
                .cartItem(cartItem)
                .product(product)
                .effectivePrice(effectivePrice)
                .itemTotal(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }

    private BigDecimal calculateCartTotal(List<CartItemWithProductDTO> items, UserDTO user) {
        return items.stream()
                .map(CartItemWithProductDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}