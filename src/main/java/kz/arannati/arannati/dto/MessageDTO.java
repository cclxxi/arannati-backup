package kz.arannati.arannati.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String content;

    // Отправитель
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private String senderRole;

    // Получатель
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private String recipientRole;

    // Статус сообщения
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Новые поля для поддержки чатов
    private String chatId;
    private String messageType;
    private boolean isBroadcast;
    private boolean isSystemMessage;
    private Long originalMessageId;
    private Long broadcastRespondedBy;

    /**
     * Проверяет, является ли сообщение от текущего пользователя
     */
    public boolean isFromUser(Long userId) {
        return senderId.equals(userId);
    }

    /**
     * Проверяет, является ли сообщение системным
     */
    public boolean isSystem() {
        return isSystemMessage;
    }

    /**
     * Возвращает CSS класс для типа сообщения
     */
    public String getMessageTypeClass() {
        if (isSystemMessage) {
            return "system-message";
        }

        switch (messageType) {
            case "SUPPORT_REQUEST":
                return "support-request";
            case "SYSTEM_DECLINE":
                return "decline-message";
            case "DIRECT":
            default:
                return "direct-message";
        }
    }

    /**
     * Возвращает иконку для типа сообщения
     */
    public String getMessageIcon() {
        if (isSystemMessage) {
            return "fas fa-cog";
        }

        switch (messageType) {
            case "SUPPORT_REQUEST":
                return "fas fa-headset";
            case "SYSTEM_DECLINE":
                return "fas fa-times-circle";
            case "DIRECT":
            default:
                return "fas fa-comment";
        }
    }

    /**
     * Возвращает форматированное время для отображения
     */
    public String getFormattedTime() {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime messageTime = createdAt;

        // Если сегодня - показываем только время
        if (messageTime.toLocalDate().equals(now.toLocalDate())) {
            return messageTime.getHour() + ":" +
                    String.format("%02d", messageTime.getMinute());
        }

        // Если вчера
        if (messageTime.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            return "Вчера " + messageTime.getHour() + ":" +
                    String.format("%02d", messageTime.getMinute());
        }

        // Если в этом году
        if (messageTime.getYear() == now.getYear()) {
            return messageTime.getDayOfMonth() + "." +
                    messageTime.getMonthValue() + " " +
                    messageTime.getHour() + ":" +
                    String.format("%02d", messageTime.getMinute());
        }

        // Полная дата
        return messageTime.getDayOfMonth() + "." +
                messageTime.getMonthValue() + "." +
                messageTime.getYear() + " " +
                messageTime.getHour() + ":" +
                String.format("%02d", messageTime.getMinute());
    }

    /**
     * Возвращает сокращенный контент для предпросмотра
     */
    public String getPreviewContent(int maxLength) {
        if (content == null) {
            return "";
        }

        if (content.length() <= maxLength) {
            return content;
        }

        return content.substring(0, maxLength) + "...";
    }

    /**
     * Проверяет, требует ли сообщение внимания (непрочитанное входящее)
     */
    public boolean requiresAttention(Long currentUserId) {
        return !isRead && recipientId.equals(currentUserId);
    }
}