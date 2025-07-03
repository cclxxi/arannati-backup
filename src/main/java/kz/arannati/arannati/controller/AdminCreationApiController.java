package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Temporary REST API controller for creating an admin user
 * This controller should be removed after the admin user is created
 */
@Slf4j
@RestController
@RequestMapping("/api/admin-creation")
@RequiredArgsConstructor
public class AdminCreationApiController extends BaseApiController {

    private final UserService userService;

    /**
     * Creates an admin user
     * @param email Admin email
     * @param firstName Admin first name
     * @param lastName Admin last name
     * @param password Admin password
     * @return Created admin user
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAdmin(
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String password) {
        
        try {
            // Check if email is already in use
            if (userService.existsByEmail(email)) {
                return errorResponse("Email is already in use", HttpStatus.BAD_REQUEST);
            }
            
            UserDTO adminUser = userService.createAdmin(email, firstName, lastName, password);
            log.info("Admin user created successfully: {}", email);
            return successResponse(adminUser, "Admin user created successfully");
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage());
            return errorResponse("Failed to create admin user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}