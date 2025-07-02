package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Optional;

/**
 * Base controller with common functionality for all controllers
 */
public abstract class BaseController {

    protected final UserService userService;

    /**
     * Constructor for BaseController
     * @param userService The user service
     */
    protected BaseController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the current authenticated user
     * @return The current user or null if not authenticated
     */
    protected UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByEmail(auth.getName()).orElse(null);
    }

    /**
     * Get the current authenticated admin
     * @return The current admin or null if not authenticated or not an admin
     */
    protected UserDTO getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        Optional<UserDTO> userOpt = userService.findByEmail(auth.getName());
        if (userOpt.isEmpty() || !"ADMIN".equals(userOpt.get().getRole())) {
            return null;
        }

        return userOpt.get();
    }

    /**
     * Get the current authenticated cosmetologist
     * @return The current cosmetologist or null if not authenticated or not a cosmetologist
     */
    protected UserDTO getCurrentCosmetologist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        Optional<UserDTO> userOpt = userService.findByEmail(auth.getName());
        if (userOpt.isEmpty() ||
                (!"COSMETOLOGIST".equals(userOpt.get().getRole()) && !"ADMIN".equals(userOpt.get().getRole()))) {
            return null;
        }

        return userOpt.get();
    }

    /**
     * Create a standard error response
     * @param message The error message
     * @return A ResponseEntity with a bad request status and the error message
     */
    protected ResponseEntity<Map<String, Object>> errorResponse(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    /**
     * Create a standard success response
     * @param message The success message
     * @return A ResponseEntity with an OK status and the success message
     */
    protected ResponseEntity<Map<String, Object>> successResponse(String message) {
        return ResponseEntity.ok(Map.of("success", true, "message", message));
    }

    /**
     * Create a standard success response with additional data
     * @param message The success message
     * @param data Additional data to include in the response
     * @return A ResponseEntity with an OK status, the success message, and the additional data
     */
    protected ResponseEntity<Map<String, Object>> successResponse(String message, Map<String, Object> data) {
        Map<String, Object> response = Map.of(
                "success", true,
                "message", message
        );

        // Combine the response map with the additional data
        return ResponseEntity.ok(
            data.entrySet().stream()
                .collect(
                    () -> new java.util.HashMap<>(response),
                    (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                    java.util.HashMap::putAll
                )
        );
    }
}
