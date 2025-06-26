package kz.arannati.arannati.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;

    @NotBlank(message = "Message content is required")
    @Size(max = 5000, message = "Message content cannot exceed 5000 characters")
    private String content;

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    private String senderName;
    private String senderEmail;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    private String recipientName;
    private String recipientEmail;

    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String chatId;
    private String messageType;
    private boolean isBroadcast;
    private boolean isSystemMessage;
}
