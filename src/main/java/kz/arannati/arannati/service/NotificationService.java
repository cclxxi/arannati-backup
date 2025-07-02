package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.NotificationDTO;
import kz.arannati.arannati.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Long userId, String title, String message, NotificationType type);
    NotificationDTO createNotification(Long userId, String title, String message, NotificationType type, Long relatedEntityId, String relatedEntityType);
    List<NotificationDTO> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationDTO> findUnreadByUserId(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    long countUnreadByUserId(Long userId);
    void notifyPriceReduction(Long productId, List<Long> userIds);
    void notifyNewOrder(Long orderId);
    void notifyNewReview(Long productId, Long reviewId);
}