package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.NotificationDTO;
import kz.arannati.arannati.entity.Notification;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.enums.NotificationType;
import kz.arannati.arannati.repository.NotificationRepository;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationDTO createNotification(Long userId, String title, String message, NotificationType type) {
        return createNotification(userId, title, message, type, null, null);
    }

    @Override
    public NotificationDTO createNotification(Long userId, String title, String message, NotificationType type,
                                              Long relatedEntityId, String relatedEntityType) {
        User user = new User();
        user.setId(userId);

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Created notification for user {}: {}", userId, title);

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findUnreadByUserId(Long userId) {
        return notificationRepository.findByUserIdAndReadIsFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countByUserIdAndReadIsFalse(userId);
    }

    @Override
    public void notifyPriceReduction(Long productId, List<Long> userIds) {
        userIds.forEach(userId -> {
            createNotification(
                    userId,
                    "Снижение цены!",
                    "Цена на товар из вашего избранного была снижена",
                    NotificationType.PRODUCT_PRICE_REDUCED,
                    productId,
                    "Product"
            );
        });
    }

    @Override
    public void notifyNewOrder(Long orderId) {
        // Notify all admins about new order
        List<User> admins = userRepository.findByRoleNameAndActiveIsTrue("ADMIN");

        for (User admin : admins) {
            createNotification(
                    admin.getId(),
                    "Новый заказ",
                    "Поступил новый заказ №" + orderId,
                    NotificationType.ORDER_CREATED,
                    orderId,
                    "Order"
            );
        }

        log.info("Notified {} admins about new order {}", admins.size(), orderId);
    }

    @Override
    public void notifyNewReview(Long productId, Long reviewId) {
        // Notify all admins about new review
        List<User> admins = userRepository.findByRoleNameAndActiveIsTrue("ADMIN");

        for (User admin : admins) {
            createNotification(
                    admin.getId(),
                    "Новый отзыв",
                    "Добавлен новый отзыв на товар",
                    NotificationType.PRODUCT_REVIEW_ADDED,
                    reviewId,
                    "Review"
            );
        }

        log.info("Notified {} admins about new review for product {}", admins.size(), productId);
    }

    private NotificationDTO convertToDto(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
