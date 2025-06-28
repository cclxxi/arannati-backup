package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import java.util.List;

/**
 * Сервис для управления чатами и сообщениями
 */
public interface ChatService {

    /**
     * Отправляет прямое сообщение между пользователями
     */
    MessageDTO sendDirectMessage(Long senderId, Long recipientId, String content);

    /**
     * Отправляет сообщение админу от косметолога/пользователя (рассылка)
     */
    MessageDTO sendSupportRequest(Long senderId, String content);

    /**
     * Админ отвечает на запрос поддержки (становится персональным чатом)
     */
    MessageDTO replyToSupportRequest(Long adminId, Long originalMessageId, String content);

    /**
     * Отправляет системное сообщение об отклонении косметолога с причиной
     */
    MessageDTO sendDeclineMessage(Long adminId, Long cosmetologistId, String reason);

    /**
     * Получает все чаты пользователя
     */
    List<ChatDTO> getUserChats(Long userId);

    /**
     * Получает историю сообщений в чате
     */
    List<MessageDTO> getChatMessages(String chatId, Long userId);

    /**
     * Получает активные запросы поддержки для админов
     */
    List<MessageDTO> getActiveSupportRequests();

    /**
     * Получает количество непрочитанных сообщений для пользователя
     */
    long getUnreadMessagesCount(Long userId);

    /**
     * Отмечает сообщения в чате как прочитанные
     */
    void markChatAsRead(String chatId, Long userId);

    /**
     * Проверяет, может ли пользователь отправлять сообщения админам
     * (есть ли у него уже открытые чаты с админами)
     */
    boolean canSendToAdmins(Long userId);

    /**
     * Получает список админов, которые НЕ имеют открытых чатов с пользователем
     */
    List<Long> getAvailableAdminsForUser(Long userId);

    /**
     * Удаляет сообщения-рассылки у других админов после ответа одного из них
     */
    void removeBroadcastsAfterResponse(Long originalMessageId, Long respondingAdminId);
}