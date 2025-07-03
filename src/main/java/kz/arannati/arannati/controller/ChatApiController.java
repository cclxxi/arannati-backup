package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.ChatService;
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
 * REST API controller for chat functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatApiController extends BaseApiController {

    private final ChatService chatService;
    private final UserService userService;

    /**
     * Get all chats for the current user
     * @return List of chats
     */
    @GetMapping("/chats")
    public ResponseEntity<Map<String, Object>> getChats() {
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

            // Get chats
            List<ChatDTO> chats = chatService.getUserChats(userId);
            
            // For admins, add active support requests
            UserDTO user = userService.findByEmail(email).orElse(null);
            if (user != null && "ADMIN".equals(user.getRole())) {
                List<MessageDTO> supportRequests = chatService.getActiveSupportRequests();
                
                Map<String, Object> data = new HashMap<>();
                data.put("chats", chats);
                data.put("supportRequests", supportRequests);
                
                return successResponse(data);
            }

            return successResponse(chats);
        } catch (Exception e) {
            log.error("Error getting chats: {}", e.getMessage());
            return errorResponse("Failed to get chats: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get messages for a specific chat
     * @param chatId Chat ID
     * @return List of messages
     */
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Map<String, Object>> getChatMessages(@PathVariable String chatId) {
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

            // Get messages
            List<MessageDTO> messages = chatService.getChatMessages(chatId, userId);
            
            // Mark chat as read
            chatService.markChatAsRead(chatId, userId);
            
            return successResponse(messages);
        } catch (Exception e) {
            log.error("Error getting chat messages: {}", e.getMessage());
            return errorResponse("Failed to get chat messages: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a direct message to another user
     * @param recipientId Recipient user ID
     * @param content Message content
     * @return Created message
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam Long recipientId,
            @RequestParam String content) {
        
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

            // Send message
            MessageDTO message = chatService.sendDirectMessage(userId, recipientId, content);
            
            Map<String, Object> data = new HashMap<>();
            data.put("message", message);
            data.put("chatId", message.getChatId());
            
            return successResponse(data, "Message sent successfully");
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return errorResponse("Failed to send message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a support request to admins
     * @param content Message content
     * @return Success message
     */
    @PostMapping("/support")
    public ResponseEntity<Map<String, Object>> sendSupportRequest(@RequestParam String content) {
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

            // Check if user is admin
            UserDTO user = userService.findByEmail(email).orElse(null);
            if (user != null && "ADMIN".equals(user.getRole())) {
                return errorResponse("Admins cannot send support requests", HttpStatus.BAD_REQUEST);
            }

            // Check if user can send to admins
            boolean canSend = chatService.canSendToAdmins(userId);
            if (!canSend) {
                return errorResponse("You already have open chats with admins", HttpStatus.BAD_REQUEST);
            }

            // Send support request
            MessageDTO message = chatService.sendSupportRequest(userId, content);
            
            return successResponse(null, "Your request has been sent to administrators");
        } catch (Exception e) {
            log.error("Error sending support request: {}", e.getMessage());
            return errorResponse("Failed to send support request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Reply to a support request (admin only)
     * @param originalMessageId Original message ID
     * @param content Reply content
     * @return Created message
     */
    @PostMapping("/support/reply")
    public ResponseEntity<Map<String, Object>> replySupportRequest(
            @RequestParam Long originalMessageId,
            @RequestParam String content) {
        
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

            // Check if user is admin
            if (!hasRole("ADMIN")) {
                return errorResponse("Only admins can reply to support requests", HttpStatus.FORBIDDEN);
            }

            // Reply to support request
            MessageDTO reply = chatService.replyToSupportRequest(userId, originalMessageId, content);
            
            Map<String, Object> data = new HashMap<>();
            data.put("message", reply);
            data.put("chatId", reply.getChatId());
            
            return successResponse(data, "Reply sent successfully");
        } catch (Exception e) {
            log.error("Error replying to support request: {}", e.getMessage());
            return errorResponse("Failed to reply to support request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a decline message to a cosmetologist (admin only)
     * @param cosmetologistId Cosmetologist user ID
     * @param reason Decline reason
     * @return Success message
     */
    @PostMapping("/decline")
    public ResponseEntity<Map<String, Object>> sendDeclineMessage(
            @RequestParam Long cosmetologistId,
            @RequestParam String reason) {
        
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

            // Check if user is admin
            if (!hasRole("ADMIN")) {
                return errorResponse("Only admins can send decline messages", HttpStatus.FORBIDDEN);
            }

            // Send decline message
            MessageDTO message = chatService.sendDeclineMessage(userId, cosmetologistId, reason);
            
            return successResponse(null, "Decline notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending decline message: {}", e.getMessage());
            return errorResponse("Failed to send decline message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mark a chat as read
     * @param chatId Chat ID
     * @return Success message
     */
    @PostMapping("/chat/{chatId}/read")
    public ResponseEntity<Map<String, Object>> markChatAsRead(@PathVariable String chatId) {
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

            // Mark chat as read
            chatService.markChatAsRead(chatId, userId);
            
            return successResponse(null, "Chat marked as read");
        } catch (Exception e) {
            log.error("Error marking chat as read: {}", e.getMessage());
            return errorResponse("Failed to mark chat as read: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the count of unread messages for the current user
     * @return Unread message count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
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

            // Get unread count
            long unreadCount = chatService.getUnreadMessagesCount(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("count", unreadCount);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting unread count: {}", e.getMessage());
            return errorResponse("Failed to get unread count: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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