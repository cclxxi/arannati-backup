package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @RestResource(path = "by-sender")
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);

    @RestResource(path = "by-recipient-list")
    List<Message> findByRecipientOrderByCreatedAtDesc(User recipient);

    @RestResource(path = "by-sender-paged")
    Page<Message> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);

    @RestResource(path = "by-recipient-paged")
    Page<Message> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    List<Message> findByRecipientAndReadIsFalseOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndReadIsFalse(User recipient);

    // Новые методы для работы с чатами

    /**
     * Получает все сообщения пользователя (отправленные и полученные)
     */
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    List<Message> findUserMessages(@Param("userId") Long userId);

    /**
     * Получает сообщения в конкретном чате, отсортированные по времени создания
     */
    List<Message> findByChatIdOrderByCreatedAtAsc(String chatId);

    /**
     * Получает непрочитанные сообщения в чате для конкретного пользователя
     */
    @Query("SELECT m FROM Message m WHERE m.chatId = :chatId AND m.recipient.id = :userId AND m.read = false")
    List<Message> findUnreadInChat(@Param("chatId") String chatId, @Param("userId") Long userId);

    /**
     * Подсчитывает непрочитанные сообщения для пользователя
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.id = :userId AND m.read = false")
    long countUnreadByRecipientId(@Param("userId") Long userId);

    /**
     * Получает ID чатов, в которых участвует пользователь
     */
    @Query("SELECT DISTINCT m.chatId FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    List<String> findChatIdsWithUser(@Param("userId") Long userId);

    /**
     * Получает активные рассылки (сообщения поддержки, на которые еще не ответили)
     */
    @Query("SELECT m FROM Message m WHERE m.broadcast = true AND m.broadcastRespondedBy IS NULL ORDER BY m.createdAt DESC")
    List<Message> findActiveBroadcasts();

    /**
     * Находит связанные рассылки по отправителю, контенту и времени создания
     */
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId AND m.content = :content " +
            "AND m.broadcast = true AND m.createdAt BETWEEN :startTime AND :endTime")
    List<Message> findRelatedBroadcasts(@Param("senderId") Long senderId,
                                        @Param("content") String content,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * Получает последние сообщения в каждом чате пользователя
     */
    @Query("SELECT m FROM Message m WHERE m.id IN (" +
            "SELECT MAX(m2.id) FROM Message m2 WHERE " +
            "(m2.sender.id = :userId OR m2.recipient.id = :userId) " +
            "AND (m2.broadcast = false OR m2.broadcastRespondedBy IS NOT NULL) " +
            "GROUP BY m2.chatId)")
    List<Message> findLastMessagesInUserChats(@Param("userId") Long userId);

    /**
     * Проверяет, есть ли активный чат между двумя пользователями
     */
    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE m.chatId = :chatId")
    boolean existsByChatId(@Param("chatId") String chatId);

    /**
     * Получает сообщения поддержки от пользователя, на которые еще не ответили
     */
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId AND m.messageType = 'SUPPORT_REQUEST' " +
            "AND m.broadcast = true AND m.broadcastRespondedBy IS NULL")
    List<Message> findUnansweredSupportRequests(@Param("userId") Long userId);
}
