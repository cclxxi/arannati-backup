package kz.arannati.arannati.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Новые поля для поддержки чатов

    @Column(name = "chat_id", nullable = false)
    private String chatId; // Идентификатор чата (генерируется на основе ID участников)

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.DIRECT; // Тип сообщения

    @Column(name = "is_broadcast", nullable = false)
    private boolean isBroadcast = false; // Является ли сообщение рассылкой для админов

    @Column(name = "broadcast_responded_by")
    private Long broadcastRespondedBy; // ID админа, который ответил на рассылку

    @Column(name = "is_system_message", nullable = false)
    private boolean isSystemMessage = false; // Системное сообщение (например, причина отклонения)

    @Column(name = "original_message_id")
    private Long originalMessageId; // Ссылка на оригинальное сообщение (для ответов)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Генерируем chatId если он не установлен
        if (chatId == null) {
            chatId = generateChatId(sender.getId(), recipient.getId());
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Генерирует идентификатор чата на основе ID участников
     */
    private String generateChatId(Long userId1, Long userId2) {
        long smaller = Math.min(userId1, userId2);
        long larger = Math.max(userId1, userId2);
        return "chat_" + smaller + "_" + larger;
    }

    /**
     * Проверяет, может ли пользователь читать это сообщение
     */
    public boolean canUserRead(Long userId) {
        return sender.getId().equals(userId) || recipient.getId().equals(userId);
    }

    /**
     * Проверяет, является ли сообщение частью чата между двумя пользователями
     */
    public boolean isChatMessage(Long userId1, Long userId2) {
        String expectedChatId = generateChatId(userId1, userId2);
        return chatId.equals(expectedChatId);
    }
}

enum MessageType {
    DIRECT,           // Прямое сообщение между пользователями
    ADMIN_BROADCAST,  // Рассылка косметологу/пользователю от админа
    SUPPORT_REQUEST,  // Запрос в поддержку/админскую службу
    SYSTEM_DECLINE    // Системное сообщение об отклонении заявки
}