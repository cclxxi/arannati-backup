package kz.arannati.arannati.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private String chatId;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserEmail;
    private String otherUserRole;
    private boolean otherUserVerified;

    // Последнее сообщение в чате
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean lastMessageFromMe;
    private boolean hasUnreadMessages;
    private long unreadCount;

    // Информация о типе чата
    private ChatType chatType;
    private boolean isSystemChat; // Чат с системными сообщениями

    public enum ChatType {
        ADMIN_USER,        // Чат между админом и обычным пользователем
        ADMIN_COSMETOLOGIST, // Чат между админом и косметологом
        SUPPORT_CHAT,      // Чат поддержки
        SYSTEM_CHAT        // Системный чат (уведомления)
    }

    /**
     * Определяет тип чата на основе ролей участников
     */
    public static ChatType determineChatType(String userRole1, String userRole2) {
        if ("ADMIN".equals(userRole1) || "ADMIN".equals(userRole2)) {
            String otherRole = "ADMIN".equals(userRole1) ? userRole2 : userRole1;

            if ("COSMETOLOGIST".equals(otherRole)) {
                return ChatType.ADMIN_COSMETOLOGIST;
            } else if ("USER".equals(otherRole)) {
                return ChatType.ADMIN_USER;
            }
        }

        return ChatType.SUPPORT_CHAT;
    }

    /**
     * Возвращает иконку для типа чата
     */
    public String getChatIcon() {
        switch (chatType) {
            case ADMIN_COSMETOLOGIST:
                return "fas fa-spa";
            case ADMIN_USER:
                return "fas fa-user";
            case SUPPORT_CHAT:
                return "fas fa-headset";
            case SYSTEM_CHAT:
                return "fas fa-cog";
            default:
                return "fas fa-comment";
        }
    }

    /**
     * Возвращает цвет для типа чата
     */
    public String getChatColor() {
        return switch (chatType) {
            case ADMIN_COSMETOLOGIST -> "purple";
            case ADMIN_USER -> "blue";
            case SUPPORT_CHAT -> "green";
            case SYSTEM_CHAT -> "gray";
            default -> "coral";
        };
    }
}