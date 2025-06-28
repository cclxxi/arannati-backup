package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.dto.WishlistItemWithProductDTO;
import kz.arannati.arannati.service.ProductService;
import kz.arannati.arannati.service.UserService;
import kz.arannati.arannati.service.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for handling wishlist functionality
 */
@Slf4j
@Controller
public class WishlistController extends BaseController {

    private final WishlistService wishlistService;
    private final ProductService productService;

    /**
     * Constructor for WishlistController
     * @param userService The user service
     * @param wishlistService The wishlist service
     * @param productService The product service
     */
    public WishlistController(UserService userService, 
                             WishlistService wishlistService,
                             ProductService productService) {
        super(userService);
        this.wishlistService = wishlistService;
        this.productService = productService;
    }

    /**
     * Wishlist page
     */
    @GetMapping("/wishlist")
    @PreAuthorize("isAuthenticated()")
    public String wishlistPage(Model model) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<WishlistDTO> wishlistItems = wishlistService.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<WishlistItemWithProductDTO> wishlistWithProducts = wishlistItems.stream()
                .map(this::enrichWishlistItemWithProduct)
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("wishlistItems", wishlistWithProducts);
        model.addAttribute("currentUser", user);

        return "wishlist/index";
    }

    /**
     * Add product to wishlist
     */
    @PostMapping("/api/wishlist/add")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> addToWishlist(@RequestParam Long productId) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return errorResponse("Необходимо войти в систему");
        }

        try {
            // Check if product exists
            Optional<ProductDTO> productOpt = productService.findById(productId);
            if (productOpt.isEmpty() || !productOpt.get().isActive()) {
                return errorResponse("Товар не найден");
            }

            // Check if already in wishlist
            if (wishlistService.existsByUserIdAndProductId(user.getId(), productId)) {
                return errorResponse("Товар уже в избранном");
            }

            WishlistDTO wishlistItem = WishlistDTO.builder()
                    .userId(user.getId())
                    .productId(productId)
                    .build();

            wishlistService.save(wishlistItem);

            return successResponse("Товар добавлен в избранное");

        } catch (Exception e) {
            log.error("Error adding product to wishlist", e);
            return errorResponse("Ошибка добавления в избранное");
        }
    }

    /**
     * Remove product from wishlist
     */
    @PostMapping("/api/wishlist/remove")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@RequestParam Long productId) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return errorResponse("Необходимо войти в систему");
        }

        try {
            wishlistService.deleteByUserIdAndProductId(user.getId(), productId);
            return successResponse("Товар удален из избранного");

        } catch (Exception e) {
            log.error("Error removing product from wishlist", e);
            return errorResponse("Ошибка удаления из избранного");
        }
    }

    /**
     * Toggle wishlist status (add/remove)
     */
    @PostMapping("/api/wishlist/toggle")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@RequestParam Long productId) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return errorResponse("Необходимо войти в систему");
        }

        try {
            boolean inWishlist = wishlistService.existsByUserIdAndProductId(user.getId(), productId);

            if (inWishlist) {
                wishlistService.deleteByUserIdAndProductId(user.getId(), productId);
                return successResponse("Товар удален из избранного", Map.of("inWishlist", false));
            } else {
                Optional<ProductDTO> productOpt = productService.findById(productId);
                if (productOpt.isEmpty() || !productOpt.get().isActive()) {
                    return errorResponse("Товар не найден");
                }

                WishlistDTO wishlistItem = WishlistDTO.builder()
                        .userId(user.getId())
                        .productId(productId)
                        .build();

                wishlistService.save(wishlistItem);
                return successResponse("Товар добавлен в избранное", Map.of("inWishlist", true));
            }

        } catch (Exception e) {
            log.error("Error toggling wishlist", e);
            return errorResponse("Ошибка изменения избранного");
        }
    }

    private WishlistItemWithProductDTO enrichWishlistItemWithProduct(WishlistDTO wishlistItem) {
        Optional<ProductDTO> productOpt = productService.findById(wishlistItem.getProductId());
        if (productOpt.isEmpty()) {
            return null;
        }

        return WishlistItemWithProductDTO.builder()
                .wishlistItem(wishlistItem)
                .product(productOpt.get())
                .build();
    }
}
