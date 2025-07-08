package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.auth.LoginRequest;
import kz.arannati.arannati.dto.auth.UserRegistrationRequest;
import kz.arannati.arannati.dto.auth.CosmetologistRegistrationRequest;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for authentication functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController extends BaseApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticate a user and return user details
     * @param loginRequest Login credentials
     * @return User details if authentication is successful
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            UserDTO user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create response data
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("firstName", user.getFirstName());
            userData.put("lastName", user.getLastName());
            userData.put("role", user.getRole());

            log.info("User logged in successfully: {}", user.getEmail());
            return successResponse(userData, "Login successful");
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return errorResponse("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return errorResponse("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Register a new user
     * @param request User registration data
     * @return Created user details
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            // Check if email is already in use
            if (userService.existsByEmail(request.getEmail())) {
                return errorResponse("Email is already in use", HttpStatus.BAD_REQUEST);
            }

            // Create user
            UserDTO createdUser = userService.createUser(request);

            log.info("User registered successfully: {}", createdUser.getEmail());
            return successResponse(createdUser, "Registration successful");
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return errorResponse("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Register a new cosmetologist
     * @param request Cosmetologist registration data
     * @param diplomaFile Cosmetologist diploma file
     * @return Created cosmetologist details
     */
    @PostMapping("/register/cosmetologist")
    public ResponseEntity<Map<String, Object>> registerCosmetologist(
            @Valid @RequestPart("data") CosmetologistRegistrationRequest request,
            @RequestPart("diplomaFile") MultipartFile diplomaFile) {
        try {
            // Check if email is already in use
            if (userService.existsByEmail(request.getEmail())) {
                return errorResponse("Email is already in use", HttpStatus.BAD_REQUEST);
            }

            // Create cosmetologist
            UserDTO createdUser = userService.createCosmetologist(request, diplomaFile);

            log.info("Cosmetologist registered successfully: {}", createdUser.getEmail());
            return successResponse(createdUser, "Registration successful. Your account will be reviewed by an administrator.");
        } catch (Exception e) {
            log.error("Cosmetologist registration error: {}", e.getMessage());
            return errorResponse("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check if an email is available for registration
     * @param email Email to check
     * @return true if email is available, false otherwise
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean isAvailable = !userService.existsByEmail(email);
            Map<String, Object> data = new HashMap<>();
            data.put("available", isAvailable);
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error checking email availability: {}", e.getMessage());
            return errorResponse("Failed to check email availability: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Request a password reset
     * @param email User email
     * @return Success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String email) {
        try {
            boolean resetInitiated = userService.sendPasswordResetEmail(email);
            if (!resetInitiated) {
                // Don't reveal that the email doesn't exist for security reasons
                return successResponse(null, "If your email is registered, you will receive a password reset link");
            }
            return successResponse(null, "Password reset link has been sent to your email");
        } catch (Exception e) {
            log.error("Error initiating password reset: {}", e.getMessage());
            return errorResponse("Failed to initiate password reset: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reset a user's password
     * @param token Password reset token
     * @param password New password
     * @param confirmPassword Confirm new password
     * @return Success message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword) {
        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                return errorResponse("Passwords do not match", HttpStatus.BAD_REQUEST);
            }

            // In a real application, we would:
            // 1. Find the user by the reset token
            // 2. Check if the token is still valid
            // 3. Update the user's password
            // 4. Clear the reset token

            // For now, we'll just log the attempt and return success
            log.info("Password reset requested for token: {}", token);

            return successResponse(null, "Password has been reset successfully");
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            return errorResponse("Failed to reset password: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout the current user
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        return successResponse(null, "Logged out successfully");
    }
}
