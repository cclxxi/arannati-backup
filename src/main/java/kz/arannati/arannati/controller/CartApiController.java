package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.CartDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.CartService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API controller for cart functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartApiController extends BaseApiController {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Get the count of items in the user's cart
     * @return The count of items in the user's cart
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartCount() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return 0
        if (email == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("count", 0);
            return successResponse(data);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                Map<String, Object> data = new HashMap<>();
                data.put("count", 0);
                return successResponse(data);
            }

            // Get the total quantity of items in the user's cart
            Integer totalQuantity = cartService.getTotalQuantityByUserId(userId);

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("count", totalQuantity != null ? totalQuantity : 0);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting cart count: {}", e.getMessage());
            return errorResponse("Failed to get cart count: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all items in the user's cart
     * @return List of cart items
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCartItems() {
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

            // Get active cart items
            List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(userId);

            return successResponse(cartItems);
        } catch (Exception e) {
            log.error("Error getting cart items: {}", e.getMessage());
            return errorResponse("Failed to get cart items: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add an item to the cart or update quantity if it already exists
     * @param cartDTO Cart item data
     * @return Updated cart item
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartDTO cartDTO) {
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

            // Set the user ID in the cart item
            cartDTO.setUserId(userId);

            // Check if the item already exists in the cart
            Optional<CartDTO> existingCartItem = cartService.findByUserIdAndProductId(userId, cartDTO.getProductId());

            if (existingCartItem.isPresent()) {
                // Update the quantity of the existing item
                CartDTO updatedCartItem = existingCartItem.get();
                updatedCartItem.setQuantity(updatedCartItem.getQuantity() + cartDTO.getQuantity());
                
                // Save the updated item
                CartDTO savedCartItem = cartService.save(updatedCartItem);
                return successResponse(savedCartItem, "Cart updated successfully");
            } else {
                // Save the new item
                CartDTO savedCartItem = cartService.save(cartDTO);
                return successResponse(savedCartItem, "Item added to cart");
            }
        } catch (Exception e) {
            log.error("Error adding item to cart: {}", e.getMessage());
            return errorResponse("Failed to add item to cart: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update the quantity of an item in the cart
     * @param id Cart item ID
     * @param cartDTO Cart item data with updated quantity
     * @return Updated cart item
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable Long id, @RequestBody CartDTO cartDTO) {
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

            // Find the cart item
            Optional<CartDTO> existingCartItem = cartService.findById(id);

            if (existingCartItem.isEmpty()) {
                return errorResponse("Cart item not found", HttpStatus.NOT_FOUND);
            }

            // Check if the cart item belongs to the user
            CartDTO item = existingCartItem.get();
            if (!item.getUserId().equals(userId)) {
                return errorResponse("Unauthorized access to cart item", HttpStatus.FORBIDDEN);
            }

            // Update the quantity
            item.setQuantity(cartDTO.getQuantity());
            
            // Save the updated item
            CartDTO savedCartItem = cartService.save(item);
            return successResponse(savedCartItem, "Cart updated successfully");
        } catch (Exception e) {
            log.error("Error updating cart item: {}", e.getMessage());
            return errorResponse("Failed to update cart item: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove an item from the cart
     * @param id Cart item ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable Long id) {
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

            // Find the cart item
            Optional<CartDTO> existingCartItem = cartService.findById(id);

            if (existingCartItem.isEmpty()) {
                return errorResponse("Cart item not found", HttpStatus.NOT_FOUND);
            }

            // Check if the cart item belongs to the user
            CartDTO item = existingCartItem.get();
            if (!item.getUserId().equals(userId)) {
                return errorResponse("Unauthorized access to cart item", HttpStatus.FORBIDDEN);
            }

            // Delete the item
            cartService.deleteById(id);
            return successResponse(null, "Item removed from cart");
        } catch (Exception e) {
            log.error("Error removing item from cart: {}", e.getMessage());
            return errorResponse("Failed to remove item from cart: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Clear the user's cart
     * @return Success message
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearCart() {
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

            // Delete all cart items for the user
            cartService.deleteAllByUserId(userId);
            return successResponse(null, "Cart cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage());
            return errorResponse("Failed to clear cart: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
}