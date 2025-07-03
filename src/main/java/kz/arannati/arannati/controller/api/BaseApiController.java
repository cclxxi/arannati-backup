package kz.arannati.arannati.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Base controller for all REST API controllers
 * Provides common functionality for API controllers
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public abstract class BaseApiController {

    /**
     * Creates a success response with the given data
     * @param data The data to include in the response
     * @return A ResponseEntity with the data and HTTP status 200 (OK)
     */
    protected <T> ResponseEntity<Map<String, Object>> successResponse(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a success response with the given data and message
     * @param data The data to include in the response
     * @param message The message to include in the response
     * @return A ResponseEntity with the data, message, and HTTP status 200 (OK)
     */
    protected <T> ResponseEntity<Map<String, Object>> successResponse(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates an error response with the given message
     * @param message The error message
     * @param status The HTTP status code
     * @return A ResponseEntity with the error message and the given HTTP status
     */
    protected ResponseEntity<Map<String, Object>> errorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Gets the current authenticated user's email
     * @return The email of the authenticated user, or null if not authenticated
     */
    protected String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Checks if the current user has the given role
     * @param role The role to check for (without the "ROLE_" prefix)
     * @return true if the user has the role, false otherwise
     */
    protected boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}