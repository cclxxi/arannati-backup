package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.CartService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling cart-related API endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Get the count of items in the user's cart
     * @return The count of items in the user's cart
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        // If user is not authenticated, return 0
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }

        try {
            // Get authenticated user's email
            String email = authentication.getName();

            // Get user ID from email (this would typically be done through a UserService)
            // For now, we'll use a placeholder approach
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                response.put("count", 0);
                return ResponseEntity.ok(response);
            }

            // Get the total quantity of items in the user's cart
            Integer totalQuantity = cartService.getTotalQuantityByUserId(userId);

            // If totalQuantity is null, return 0
            response.put("count", totalQuantity != null ? totalQuantity : 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart count: {}", e.getMessage());
            response.put("count", 0);
            return ResponseEntity.ok(response);
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
