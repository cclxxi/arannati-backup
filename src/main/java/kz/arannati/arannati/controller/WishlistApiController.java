package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.dto.WishlistItemWithProductDTO;
import kz.arannati.arannati.service.ProductService;
import kz.arannati.arannati.service.UserService;
import kz.arannati.arannati.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API controller for wishlist functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistApiController extends BaseApiController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserService userService;

    /**
     * Get all items in the user's wishlist
     * @return List of wishlist items with product details
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getWishlistItems() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get wishlist items
            List<WishlistDTO> wishlistItems = wishlistService.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Enrich wishlist items with product details
            List<WishlistItemWithProductDTO> wishlistWithProducts = wishlistItems.stream()
                    .map(this::enrichWishlistItemWithProduct)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return successResponse(wishlistWithProducts);
        } catch (Exception e) {
            log.error("Error getting wishlist items: {}", e.getMessage());
            return errorResponse("Failed to get wishlist items: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if a product is in the user's wishlist
     * @param productId Product ID
     * @return true if the product is in the wishlist, false otherwise
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkWishlistItem(@PathVariable Long productId) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return false
        if (email == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("inWishlist", false);
            return successResponse(data);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                Map<String, Object> data = new HashMap<>();
                data.put("inWishlist", false);
                return successResponse(data);
            }

            // Check if product is in wishlist
            boolean inWishlist = wishlistService.existsByUserIdAndProductId(userId, productId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("inWishlist", inWishlist);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error checking wishlist item: {}", e.getMessage());
            return errorResponse("Failed to check wishlist item: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add a product to the wishlist
     * @param productId Product ID
     * @return Success message
     */
    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(@PathVariable Long productId) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Check if product exists
            Optional<ProductDTO> productOpt = productService.findById(productId);
            if (productOpt.isEmpty() || !productOpt.get().isActive()) {
                return errorResponse("Product not found", HttpStatus.NOT_FOUND);
            }

            // Check if already in wishlist
            if (wishlistService.existsByUserIdAndProductId(userId, productId)) {
                return errorResponse("Product already in wishlist", HttpStatus.BAD_REQUEST);
            }

            // Create wishlist item
            WishlistDTO wishlistItem = WishlistDTO.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();

            // Save wishlist item
            wishlistService.save(wishlistItem);

            return successResponse(null, "Product added to wishlist");
        } catch (Exception e) {
            log.error("Error adding product to wishlist: {}", e.getMessage());
            return errorResponse("Failed to add product to wishlist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove a product from the wishlist
     * @param productId Product ID
     * @return Success message
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@PathVariable Long productId) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Delete wishlist item
            wishlistService.deleteByUserIdAndProductId(userId, productId);

            return successResponse(null, "Product removed from wishlist");
        } catch (Exception e) {
            log.error("Error removing product from wishlist: {}", e.getMessage());
            return errorResponse("Failed to remove product from wishlist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggle a product in the wishlist (add if not present, remove if present)
     * @param productId Product ID
     * @return Success message and updated status
     */
    @PutMapping("/toggle/{productId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@PathVariable Long productId) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Check if product is in wishlist
            boolean inWishlist = wishlistService.existsByUserIdAndProductId(userId, productId);

            if (inWishlist) {
                // Remove from wishlist
                wishlistService.deleteByUserIdAndProductId(userId, productId);
                
                Map<String, Object> data = new HashMap<>();
                data.put("inWishlist", false);
                
                return successResponse(data, "Product removed from wishlist");
            } else {
                // Check if product exists
                Optional<ProductDTO> productOpt = productService.findById(productId);
                if (productOpt.isEmpty() || !productOpt.get().isActive()) {
                    return errorResponse("Product not found", HttpStatus.NOT_FOUND);
                }

                // Add to wishlist
                WishlistDTO wishlistItem = WishlistDTO.builder()
                        .userId(userId)
                        .productId(productId)
                        .build();

                wishlistService.save(wishlistItem);
                
                Map<String, Object> data = new HashMap<>();
                data.put("inWishlist", true);
                
                return successResponse(data, "Product added to wishlist");
            }
        } catch (Exception e) {
            log.error("Error toggling wishlist: {}", e.getMessage());
            return errorResponse("Failed to toggle wishlist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to get user ID from email
     * @param email The user's email
     * @return The user's ID or null if the user is not found
     */
    private Long getUserIdFromEmail(String email) {
        Optional<UserDTO> userOpt = userService.findByEmail(email);
        return userOpt.map(UserDTO::getId).orElse(null);
    }

    /**
     * Helper method to enrich a wishlist item with product details
     * @param wishlistItem The wishlist item
     * @return The wishlist item with product details
     */
    private WishlistItemWithProductDTO enrichWishlistItemWithProduct(WishlistDTO wishlistItem) {
        Optional<ProductDTO> productOpt = productService.findById(wishlistItem.getProductId());
        return productOpt.map(productDTO -> WishlistItemWithProductDTO.builder()
                .wishlistItem(wishlistItem)
                .product(productDTO)
                .build()).orElse(null);

    }
}