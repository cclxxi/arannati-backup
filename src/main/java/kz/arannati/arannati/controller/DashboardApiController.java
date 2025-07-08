package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.ChatService;
import kz.arannati.arannati.service.MessageService;
import kz.arannati.arannati.service.OrderService;
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
 * REST API controller for dashboard functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController extends BaseApiController {

    private final UserService userService;
    private final OrderService orderService;
    private final MessageService messageService;
    private final ChatService chatService;

    /**
     * Get dashboard data for the authenticated user
     * @return Dashboard data based on user role
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return guest dashboard data
        if (email == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("userType", "guest");
            return successResponse(data);
        }

        try {
            // Get authenticated user
            Optional<UserDTO> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            UserDTO user = userOpt.get();
            
            // Get order history
            List<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(user.getId());
            
            // Get unread message count
            long unreadMessageCount = messageService.countUnreadByRecipientId(user.getId());
            
            // Get messages
            List<MessageDTO> receivedMessages = messageService.findByRecipientId(user.getId());
            List<MessageDTO> sentMessages = messageService.findBySenderId(user.getId());
            
            // Get chats
            List<ChatDTO> chats = chatService.getUserChats(user.getId());
            
            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("orders", orders);
            data.put("unreadMessageCount", unreadMessageCount);
            data.put("receivedMessages", receivedMessages);
            data.put("sentMessages", sentMessages);
            data.put("chats", chats);
            
            // Add role-specific data
            String roleName = user.getRole();
            data.put("userType", roleName.toLowerCase());
            
            if ("ADMIN".equals(roleName)) {
                // Add users list for admin dashboard
                List<UserDTO> users = userService.findAll();
                data.put("users", users);
                
                // Add pending cosmetologists for admin dashboard
                List<UserDTO> pendingCosmetologists = userService.findPendingCosmetologists();
                data.put("pendingCosmetologists", pendingCosmetologists);
            }
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting dashboard data: {}", e.getMessage());
            return errorResponse("Failed to get dashboard data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get user profile data
     * @return User profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get authenticated user
            Optional<UserDTO> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            UserDTO user = userOpt.get();
            
            return successResponse(user);
        } catch (Exception e) {
            log.error("Error getting user profile: {}", e.getMessage());
            return errorResponse("Failed to get user profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get order history for the authenticated user
     * @return Order history
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get authenticated user
            Optional<UserDTO> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            UserDTO user = userOpt.get();
            
            // Get order history
            List<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(user.getId());
            
            return successResponse(orders);
        } catch (Exception e) {
            log.error("Error getting order history: {}", e.getMessage());
            return errorResponse("Failed to get order history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get messages for the authenticated user
     * @return Messages
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get authenticated user
            Optional<UserDTO> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            UserDTO user = userOpt.get();
            
            // Get messages
            List<MessageDTO> receivedMessages = messageService.findByRecipientId(user.getId());
            List<MessageDTO> sentMessages = messageService.findBySenderId(user.getId());
            
            // Get unread message count
            long unreadMessageCount = messageService.countUnreadByRecipientId(user.getId());
            
            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("receivedMessages", receivedMessages);
            data.put("sentMessages", sentMessages);
            data.put("unreadMessageCount", unreadMessageCount);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting messages: {}", e.getMessage());
            return errorResponse("Failed to get messages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mark a message as read
     * @param id Message ID
     * @return Success message
     */
    @PostMapping("/messages/{id}/read")
    public ResponseEntity<Map<String, Object>> markMessageAsRead(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            boolean success = messageService.markAsRead(id);
            if (success) {
                return successResponse(null, "Message marked as read");
            } else {
                return errorResponse("Failed to mark message as read", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage());
            return errorResponse("Failed to mark message as read: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a message to another user
     * @param userId Recipient user ID
     * @param message Message content
     * @return Created message
     */
    @PostMapping("/messages/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam Long userId,
            @RequestParam String message) {
        
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get authenticated user
            Optional<UserDTO> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Send the message
            MessageDTO sentMessage = messageService.sendMessage(userOpt.get().getId(), userId, message);
            
            return successResponse(sentMessage, "Message sent successfully");
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return errorResponse("Failed to send message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}