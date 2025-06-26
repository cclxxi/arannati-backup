package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Temporary controller for creating an admin user
 * This controller should be removed after the admin user is created
 */
@Slf4j
@RestController
@RequestMapping("/api/admin-creation")
@RequiredArgsConstructor
public class AdminCreationController {

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
    public ResponseEntity<UserDTO> createAdmin(
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String password) {
        
        try {
            UserDTO adminUser = userService.createAdmin(email, firstName, lastName, password);
            log.info("Admin user created successfully: {}", email);
            return ResponseEntity.ok(adminUser);
        } catch (Exception e) {
            log.error("Failed to create admin user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}