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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ChatService chatService;

    /**
     * Главная страница административной панели
     */
    @GetMapping
    public String dashboard(Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        // Статистика пользователей
        long totalUsers = userService.countByRoleAndActiveIsTrue("USER");
        long totalCosmetologists = userService.countByRoleAndActiveIsTrue("COSMETOLOGIST");
        long pendingCosmetologists = userService.findPendingCosmetologists().size();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCosmetologists", totalCosmetologists);
        model.addAttribute("pendingCosmetologists", pendingCosmetologists);

        // Активные запросы поддержки
        List<MessageDTO> supportRequests = chatService.getActiveSupportRequests();
        model.addAttribute("supportRequests", supportRequests);

        // Последние чаты админа
        List<ChatDTO> recentChats = chatService.getUserChats(admin.getId());
        model.addAttribute("recentChats", recentChats.stream().limit(5).toList());

        model.addAttribute("currentUser", admin);
        return "admin/dashboard";
    }

    /**
     * Управление пользователями
     */
    @GetMapping("/users")
    public String users(Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        List<UserDTO> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("currentUserId", admin.getId());
        model.addAttribute("currentUser", admin);

        return "admin/users";
    }

    /**
     * Подтверждение косметолога
     */
    @PostMapping("/cosmetologists/{id}/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveCosmetologist(@PathVariable Long id) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            boolean success = userService.approveCosmetologist(id);
            if (success) {
                log.info("Admin {} approved cosmetologist {}", admin.getEmail(), id);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Не удалось подтвердить косметолога"));
            }
        } catch (Exception e) {
            log.error("Error approving cosmetologist", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка подтверждения косметолога"));
        }
    }

    /**
     * Отклонение косметолога с отправкой причины
     */
    @PostMapping("/cosmetologists/{id}/decline")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> declineCosmetologist(
            @PathVariable Long id,
            @RequestParam String reason) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            // Отклоняем косметолога
            boolean declined = userService.declineCosmetologist(id, reason);
            if (!declined) {
                return ResponseEntity.badRequest().body(Map.of("error", "Не удалось отклонить косметолога"));
            }

            log.info("Admin {} declined cosmetologist {} with reason: {}", admin.getEmail(), id, reason);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("Error declining cosmetologist", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отклонения косметолога"));
        }
    }

    /**
     * Страница для отклонения косметолога с формой причины
     */
    @GetMapping("/cosmetologists/{id}/decline-form")
    public String declineCosmetologistForm(@PathVariable Long id, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<UserDTO> cosmetologist = userService.findById(id);
        if (cosmetologist.isEmpty() || !"COSMETOLOGIST".equals(cosmetologist.get().getRole())) {
            return "redirect:/admin/users";
        }

        model.addAttribute("cosmetologist", cosmetologist.get());
        model.addAttribute("defaultReason", getDefaultDeclineReason());
        model.addAttribute("currentUser", admin);

        return "admin/decline-cosmetologist";
    }

    /**
     * Переключение активности пользователя
     */
    @PostMapping("/users/{id}/toggle-active")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleUserActive(@PathVariable Long id) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            boolean success = userService.toggleActive(id);
            if (success) {
                log.info("Admin {} toggled active status for user {}", admin.getEmail(), id);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Не удалось изменить статус пользователя"));
            }
        } catch (Exception e) {
            log.error("Error toggling user active status", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка изменения статуса пользователя"));
        }
    }

    /**
     * Отправка сообщения пользователю
     */
    @PostMapping("/users/{id}/send-message")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessageToUser(
            @PathVariable Long id,
            @RequestParam String message) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            MessageDTO sentMessage = chatService.sendDirectMessage(admin.getId(), id, message);
            log.info("Admin {} sent message to user {}", admin.getEmail(), id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", sentMessage,
                    "chatId", sentMessage.getChatId()
            ));
        } catch (Exception e) {
            log.error("Error sending message to user", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отправки сообщения"));
        }
    }

    /**
     * Страница сообщений для админа
     */
    @GetMapping("/messages")
    public String messagesPage(Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        List<ChatDTO> chats = chatService.getUserChats(admin.getId());
        List<MessageDTO> supportRequests = chatService.getActiveSupportRequests();

        model.addAttribute("chats", chats);
        model.addAttribute("supportRequests", supportRequests);
        model.addAttribute("currentUser", admin);

        return "admin/messages";
    }

    /**
     * Получение списка всех пользователей для выбора получателя сообщения
     */
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserDTO> users = userService.findAll().stream()
                .filter(user -> !user.getId().equals(admin.getId())) // Исключаем текущего админа
                .toList();

        return ResponseEntity.ok(users);
    }

    /**
     * Альтернативный эндпоинт для списка пользователей (для совместимости)
     */
    @GetMapping("/users/list")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getUsersList() {
        return getAllUsers();
    }

    /**
     * Модальное окно для написания сообщения пользователю
     */
    @GetMapping("/users/{id}/message-modal")
    public String messageModal(@PathVariable Long id, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<UserDTO> user = userService.findById(id);
        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("recipient", user.get());
        model.addAttribute("currentUser", admin);

        return "admin/message-modal";
    }

    /**
     * Получение статистики для дашборда
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().build();
        }

        long totalUsers = userService.countByRoleAndActiveIsTrue("USER");
        long totalCosmetologists = userService.countByRoleAndActiveIsTrue("COSMETOLOGIST");
        long pendingCosmetologists = userService.findPendingCosmetologists().size();
        long activeSupportRequests = chatService.getActiveSupportRequests().size();
        long unreadMessages = chatService.getUnreadMessagesCount(admin.getId());

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "totalCosmetologists", totalCosmetologists,
                "pendingCosmetologists", pendingCosmetologists,
                "activeSupportRequests", activeSupportRequests,
                "unreadMessages", unreadMessages
        ));
    }

    private UserDTO getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        Optional<UserDTO> user = userService.findByEmail(email);

        if (user.isEmpty() || !"ADMIN".equals(user.get().getRole())) {
            return null;
        }

        return user.get();
    }

    private String getDefaultDeclineReason() {
        return "Ваша заявка на верификацию косметолога была отклонена.\n\n" +
                "Причина отклонения:\n" +
                "• Предоставленные документы не соответствуют требованиям\n" +
                "• Документы должны быть четкими и читаемыми\n" +
                "• Необходимо предоставить диплом или сертификат о профильном образовании\n" +
                "• Документы должны быть действующими\n\n" +
                "Пожалуйста, исправьте указанные недостатки и подайте заявку повторно.";
    }
}