package kz.arannati.arannati.config;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

/**
 * Global controller advice that adds common attributes to all models
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesAdvice {

    private final UserService userService;

    /**
     * Adds the authenticated user to all models
     * @return The authenticated user or null if not authenticated
     */
    @ModelAttribute("user")
    public UserDTO addUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, return null
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        // Get authenticated user
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.debug("Authenticated user not found in database: {}", email);
            return null;
        }

        return userOpt.get();
    }
}