package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.ChatService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    /**
     * Главная страница сообщений
     */
    @GetMapping
    public String messagesPage(Model model) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<ChatDTO> chats = chatService.getUserChats(currentUser.getId());
        model.addAttribute("chats", chats);
        model.addAttribute("currentUser", currentUser);

        // Для админов добавляем активные запросы поддержки
        if ("ADMIN".equals(currentUser.getRole())) {
            List<MessageDTO> supportRequests = chatService.getActiveSupportRequests();
            model.addAttribute("supportRequests", supportRequests);
        }

        return "messages/index";
    }

    /**
     * Страница конкретного чата
     */
    @GetMapping("/chat/{chatId}")
    public String chatPage(@PathVariable String chatId, Model model) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        List<MessageDTO> messages = chatService.getChatMessages(chatId, currentUser.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("chatId", chatId);
        model.addAttribute("currentUser", currentUser);

        // Отмечаем сообщения как прочитанные
        chatService.markChatAsRead(chatId, currentUser.getId());

        return "messages/chat";
    }

    /**
     * Отправка прямого сообщения
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestParam Long recipientId,
            @RequestParam String content) {

        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не авторизован"));
        }

        try {
            MessageDTO message = chatService.sendDirectMessage(currentUser.getId(), recipientId, content);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message,
                    "chatId", message.getChatId()
            ));
        } catch (Exception e) {
            log.error("Error sending message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отправки сообщения"));
        }
    }

    /**
     * Отправка запроса в поддержку (для косметологов и пользователей)
     */
    @PostMapping("/support")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendSupportRequest(@RequestParam String content) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не авторизован"));
        }

        if ("ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Админы не могут отправлять запросы в поддержку"));
        }

        try {
            boolean canSend = chatService.canSendToAdmins(currentUser.getId());
            if (!canSend) {
                return ResponseEntity.badRequest().body(Map.of("error", "У вас уже есть открытые чаты с админами"));
            }

            MessageDTO message = chatService.sendSupportRequest(currentUser.getId(), content);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ваш запрос отправлен администраторам"
            ));
        } catch (Exception e) {
            log.error("Error sending support request", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отправки запроса"));
        }
    }

    /**
     * Ответ админа на запрос поддержки
     */
    @PostMapping("/support/reply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> replySupportRequest(
            @RequestParam Long originalMessageId,
            @RequestParam String content) {

        UserDTO currentUser = getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            MessageDTO reply = chatService.replyToSupportRequest(currentUser.getId(), originalMessageId, content);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", reply,
                    "chatId", reply.getChatId()
            ));
        } catch (Exception e) {
            log.error("Error replying to support request", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отправки ответа"));
        }
    }

    /**
     * Отправка сообщения об отклонении косметолога
     */
    @PostMapping("/decline")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendDeclineMessage(
            @RequestParam Long cosmetologistId,
            @RequestParam String reason) {

        UserDTO currentUser = getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            MessageDTO message = chatService.sendDeclineMessage(currentUser.getId(), cosmetologistId, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Уведомление об отклонении отправлено"
            ));
        } catch (Exception e) {
            log.error("Error sending decline message", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отправки уведомления"));
        }
    }

    /**
     * Получение списка чатов (API)
     */
    @GetMapping("/api/chats")
    @ResponseBody
    public ResponseEntity<List<ChatDTO>> getChats() {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ChatDTO> chats = chatService.getUserChats(currentUser.getId());
        return ResponseEntity.ok(chats);
    }

    /**
     * Получение сообщений чата (API)
     */
    @GetMapping("/api/chat/{chatId}")
    @ResponseBody
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable String chatId) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        List<MessageDTO> messages = chatService.getChatMessages(chatId, currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    /**
     * Отметка чата как прочитанного
     */
    @PostMapping("/api/chat/{chatId}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markChatAsRead(@PathVariable String chatId) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Пользователь не авторизован"));
        }

        chatService.markChatAsRead(chatId, currentUser.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Получение количества непрочитанных сообщений
     */
    @GetMapping("/api/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        long unreadCount = chatService.getUnreadMessagesCount(currentUser.getId());
        return ResponseEntity.ok(Map.of("count", unreadCount));
    }

    /**
     * Модальное окно для написания сообщения администратору
     */
    @GetMapping("/write-admin")
    public String writeAdminModal(Model model) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        boolean canSendToAdmins = chatService.canSendToAdmins(currentUser.getId());
        model.addAttribute("canSendToAdmins", canSendToAdmins);
        model.addAttribute("currentUser", currentUser);

        return "messages/write-admin-modal";
    }

    /**
     * Модальное окно с предустановленным текстом для отклонения косметолога
     */
    @GetMapping("/decline-template/{cosmetologistId}")
    public String declineTemplate(@PathVariable Long cosmetologistId, Model model) {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/auth/login";
        }

        Optional<UserDTO> cosmetologist = userService.findById(cosmetologistId);
        if (cosmetologist.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("cosmetologist", cosmetologist.get());
        model.addAttribute("defaultReason", getDefaultDeclineReason());

        return "messages/decline-modal";
    }

    private UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        return userService.findByEmail(email).orElse(null);
    }

    private String getDefaultDeclineReason() {
        return "Предоставленные документы не соответствуют требованиям:\n\n" +
                "• Документ должен быть четким и читаемым\n" +
                "• Необходимо предоставить диплом или сертификат о профильном образовании\n" +
                "• Документ должен быть действующим\n\n" +
                "Пожалуйста, исправьте указанные недостатки и подайте заявку повторно.";
    }
}