package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.MessageService;
import kz.arannati.arannati.service.OrderService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling dashboard functionality
 */
@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final OrderService orderService;
    private final MessageService messageService;

    /**
     * Main dashboard page for authenticated users
     */
    @GetMapping
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, show guest dashboard
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "dashboard/guest";
        }

        // Get authenticated user
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.error("Authenticated user not found in database: {}", email);
            return "redirect:/auth/login";
        }

        UserDTO user = userOpt.get();

        // Add user data to model
        model.addAttribute("user", user);

        // Add order history to model
        List<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("orders", orders);

        // Determine which dashboard to show based on user role
        String roleName = user.getRole();

        // Add unread message count for all users
        long unreadMessageCount = messageService.countUnreadByRecipientId(user.getId());
        model.addAttribute("unreadMessageCount", unreadMessageCount);

        // Add messages for all users
        List<MessageDTO> receivedMessages = messageService.findByRecipientId(user.getId());
        List<MessageDTO> sentMessages = messageService.findBySenderId(user.getId());
        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);

        if ("ADMIN".equals(roleName)) {
            // Add users list for admin dashboard
            List<UserDTO> users = userService.findAll();
            model.addAttribute("users", users);

            // Add pending cosmetologists for admin dashboard
            List<UserDTO> pendingCosmetologists = userService.findPendingCosmetologists();
            model.addAttribute("pendingCosmetologists", pendingCosmetologists);

            return "dashboard/admin";
        } else if ("COSMETOLOGIST".equals(roleName)) {
            return "dashboard/cosmetologist";
        } else {
            return "dashboard/user";
        }
    }

    /**
     * Profile page for authenticated users
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/auth/login";
        }

        // Get authenticated user
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.error("Authenticated user not found in database: {}", email);
            return "redirect:/auth/login";
        }

        UserDTO user = userOpt.get();

        // Add user data to model
        model.addAttribute("user", user);

        return "dashboard/profile";
    }

    /**
     * Order history page for authenticated users
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/auth/login";
        }

        // Get authenticated user
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.error("Authenticated user not found in database: {}", email);
            return "redirect:/auth/login";
        }

        UserDTO user = userOpt.get();

        // Add user data to model
        model.addAttribute("user", user);

        // Add order history to model
        List<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("orders", orders);

        return "dashboard/orders";
    }

    /**
     * Messages page for authenticated users
     */
    @GetMapping("/messages")
    public String messages(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/auth/login";
        }

        // Get authenticated user
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.error("Authenticated user not found in database: {}", email);
            return "redirect:/auth/login";
        }

        UserDTO user = userOpt.get();

        // Add user data to model
        model.addAttribute("user", user);

        // Add messages to model
        List<MessageDTO> receivedMessages = messageService.findByRecipientId(user.getId());
        List<MessageDTO> sentMessages = messageService.findBySenderId(user.getId());
        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);

        // Add unread message count
        long unreadMessageCount = messageService.countUnreadByRecipientId(user.getId());
        model.addAttribute("unreadMessageCount", unreadMessageCount);

        return "dashboard/messages";
    }

    /**
     * Mark message as read
     */
    @PostMapping("/messages/{id}/read")
    @ResponseBody
    public ResponseEntity<String> markMessageAsRead(@PathVariable Long id) {
        boolean success = messageService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok("Message marked as read");
        } else {
            return ResponseEntity.badRequest().body("Failed to mark message as read");
        }
    }

    /**
     * Send message to user
     */
    @PostMapping("/messages/send")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@RequestParam Long userId, @RequestParam String message) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserDTO> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Send the message
        messageService.sendMessage(userOpt.get().getId(), userId, message);
        return ResponseEntity.ok("Message sent successfully");
    }
}
