package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import kz.arannati.arannati.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Message is required")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String message;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private Long relatedEntityId;

    @Size(max = 100, message = "Related entity type cannot exceed 100 characters")
    private String relatedEntityType;

    private boolean read;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
