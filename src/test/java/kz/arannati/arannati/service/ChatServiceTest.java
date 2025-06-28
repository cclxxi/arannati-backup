package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.Role;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.enums.MessageType;
import kz.arannati.arannati.repository.MessageRepository;
import kz.arannati.arannati.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User user;
    private User admin;
    private User recipient;
    private UserDTO userDTO;
    private UserDTO adminDTO;
    private UserDTO recipientDTO;
    private Message message;
    private String chatId;

    @BeforeEach
    void setUp() {
        // Set up roles
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");

        // Set up users
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setRole(userRole);
        user.setActive(true);

        admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(adminRole);
        admin.setActive(true);

        recipient = new User();
        recipient.setId(3L);
        recipient.setEmail("recipient@example.com");
        recipient.setFirstName("Recipient");
        recipient.setLastName("User");
        recipient.setRole(userRole);
        recipient.setActive(true);

        // Set up DTOs
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setFirstName("Regular");
        userDTO.setLastName("User");
        userDTO.setRole("USER");
        userDTO.setActive(true);

        adminDTO = new UserDTO();
        adminDTO.setId(2L);
        adminDTO.setEmail("admin@example.com");
        adminDTO.setFirstName("Admin");
        adminDTO.setLastName("User");
        adminDTO.setRole("ADMIN");
        adminDTO.setActive(true);

        recipientDTO = new UserDTO();
        recipientDTO.setId(3L);
        recipientDTO.setEmail("recipient@example.com");
        recipientDTO.setFirstName("Recipient");
        recipientDTO.setLastName("User");
        recipientDTO.setRole("USER");
        recipientDTO.setActive(true);

        // Set up chat ID
        chatId = "chat_1_3"; // Between user and recipient

        // Set up message
        message = Message.builder()
                .id(1L)
                .content("Test message")
                .sender(user)
                .recipient(recipient)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId(chatId)
                .messageType(MessageType.DIRECT)
                .isBroadcast(false)
                .isSystemMessage(false)
                .build();
    }

    @Test
    void sendDirectMessage_shouldSendMessage_whenUsersExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.findById(3L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // When
        MessageDTO result = chatService.sendDirectMessage(1L, 3L, "Test message");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test message");
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getRecipientId()).isEqualTo(3L);
        assertThat(result.getMessageType()).isEqualTo("DIRECT");
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void sendDirectMessage_shouldThrowException_whenSenderDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            chatService.sendDirectMessage(1L, 3L, "Test message");
        });
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendDirectMessage_shouldThrowException_whenRecipientDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(userService.findById(3L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            chatService.sendDirectMessage(1L, 3L, "Test message");
        });
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendSupportRequest_shouldSendBroadcastToAdmins_whenAdminsAvailable() {
        // Given
        Message broadcastMessage = Message.builder()
                .id(2L)
                .content("Support request")
                .sender(user)
                .recipient(admin)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId("chat_1_2")
                .messageType(MessageType.SUPPORT_REQUEST)
                .isBroadcast(true)
                .isSystemMessage(false)
                .build();

        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(userService.findByRoleAndActiveIsTrue("ADMIN")).thenReturn(Arrays.asList(adminDTO));
        when(messageRepository.findChatIdsWithUser(1L)).thenReturn(Collections.emptyList());
        when(userService.findById(2L)).thenReturn(Optional.of(adminDTO));
        when(userService.convertToEntity(adminDTO)).thenReturn(admin);
        when(messageRepository.saveAll(anyList())).thenReturn(Arrays.asList(broadcastMessage));

        // When
        MessageDTO result = chatService.sendSupportRequest(1L, "Support request");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Support request");
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getRecipientId()).isEqualTo(2L);
        assertThat(result.getMessageType()).isEqualTo("SUPPORT_REQUEST");
        assertThat(result.isBroadcast()).isTrue();
        verify(messageRepository, times(1)).saveAll(anyList());
    }

    @Test
    void sendSupportRequest_shouldThrowException_whenNoAdminsAvailable() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(chatService.getAvailableAdminsForUser(1L)).thenReturn(Collections.emptyList());

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            chatService.sendSupportRequest(1L, "Support request");
        });
        verify(messageRepository, never()).saveAll(anyList());
    }

    @Test
    void replyToSupportRequest_shouldCreateDirectMessage_whenOriginalMessageExists() {
        // Given
        Message originalMessage = Message.builder()
                .id(2L)
                .content("Support request")
                .sender(user)
                .recipient(admin)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId("chat_1_2")
                .messageType(MessageType.SUPPORT_REQUEST)
                .isBroadcast(true)
                .isSystemMessage(false)
                .build();

        Message replyMessage = Message.builder()
                .id(3L)
                .content("Reply to support request")
                .sender(admin)
                .recipient(user)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId("chat_1_2")
                .messageType(MessageType.DIRECT)
                .isBroadcast(false)
                .isSystemMessage(false)
                .originalMessageId(2L)
                .build();

        when(messageRepository.findById(2L)).thenReturn(Optional.of(originalMessage));
        when(userService.findById(2L)).thenReturn(Optional.of(adminDTO));
        when(userService.convertToEntity(adminDTO)).thenReturn(admin);
        when(messageRepository.save(any(Message.class))).thenReturn(replyMessage);

        // Mock dependencies for removeBroadcastsAfterResponse
        LocalDateTime createdAt = originalMessage.getCreatedAt();
        when(messageRepository.findRelatedBroadcasts(
                eq(originalMessage.getSender().getId()),
                eq(originalMessage.getContent()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // When
        MessageDTO result = chatService.replyToSupportRequest(2L, 2L, "Reply to support request");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Reply to support request");
        assertThat(result.getSenderId()).isEqualTo(2L);
        assertThat(result.getRecipientId()).isEqualTo(1L);
        assertThat(result.getMessageType()).isEqualTo("DIRECT");
        assertThat(result.isBroadcast()).isFalse();
        verify(messageRepository, times(2)).save(any(Message.class)); // Original message update + new message
        verify(messageRepository, times(1)).findRelatedBroadcasts(
                eq(originalMessage.getSender().getId()),
                eq(originalMessage.getContent()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void sendDeclineMessage_shouldSendSystemMessage_whenUsersExist() {
        // Given
        Message declineMessage = Message.builder()
                .id(4L)
                .content("Your application has been declined: Test reason")
                .sender(admin)
                .recipient(user)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId("chat_1_2")
                .messageType(MessageType.SYSTEM_DECLINE)
                .isBroadcast(false)
                .isSystemMessage(true)
                .build();

        when(userService.findById(2L)).thenReturn(Optional.of(adminDTO));
        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.convertToEntity(adminDTO)).thenReturn(admin);
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(messageRepository.save(any(Message.class))).thenReturn(declineMessage);

        // When
        MessageDTO result = chatService.sendDeclineMessage(2L, 1L, "Test reason");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSenderId()).isEqualTo(2L);
        assertThat(result.getRecipientId()).isEqualTo(1L);
        assertThat(result.getMessageType()).isEqualTo("SYSTEM_DECLINE");
        assertThat(result.isSystemMessage()).isTrue();
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void getUserChats_shouldReturnChats_whenUserHasMessages() {
        // Given
        List<Message> userMessages = Arrays.asList(message);
        when(userService.findById(1L)).thenReturn(Optional.of(userDTO));
        when(userService.convertToEntity(userDTO)).thenReturn(user);
        when(messageRepository.findUserMessages(1L)).thenReturn(userMessages);

        // When
        List<ChatDTO> result = chatService.getUserChats(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChatId()).isEqualTo(chatId);
        assertThat(result.get(0).getOtherUserId()).isEqualTo(3L);
        assertThat(result.get(0).getLastMessage()).isEqualTo("Test message");
    }

    @Test
    void getChatMessages_shouldReturnMessages_whenChatExists() {
        // Given
        List<Message> chatMessages = Arrays.asList(message);
        when(messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)).thenReturn(chatMessages);

        // When
        List<MessageDTO> result = chatService.getChatMessages(chatId, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent()).isEqualTo("Test message");
    }

    @Test
    void getActiveSupportRequests_shouldReturnActiveBroadcasts() {
        // Given
        Message broadcastMessage = Message.builder()
                .id(2L)
                .content("Support request")
                .sender(user)
                .recipient(admin)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatId("chat_1_2")
                .messageType(MessageType.SUPPORT_REQUEST)
                .isBroadcast(true)
                .isSystemMessage(false)
                .build();

        when(messageRepository.findActiveBroadcasts()).thenReturn(Arrays.asList(broadcastMessage));

        // When
        List<MessageDTO> result = chatService.getActiveSupportRequests();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Support request");
        assertThat(result.get(0).isBroadcast()).isTrue();
    }

    @Test
    void getUnreadMessagesCount_shouldReturnCount() {
        // Given
        when(messageRepository.countUnreadByRecipientId(1L)).thenReturn(5L);

        // When
        long result = chatService.getUnreadMessagesCount(1L);

        // Then
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void markChatAsRead_shouldMarkMessagesAsRead() {
        // Given
        List<Message> unreadMessages = Arrays.asList(message);
        when(messageRepository.findUnreadInChat(chatId, 1L)).thenReturn(unreadMessages);

        // When
        chatService.markChatAsRead(chatId, 1L);

        // Then
        assertThat(message.isRead()).isTrue();
        verify(messageRepository, times(1)).saveAll(unreadMessages);
    }

    @Test
    void canSendToAdmins_shouldReturnTrue_whenAdminsAvailable() {
        // Given
        when(userService.findByRoleAndActiveIsTrue("ADMIN")).thenReturn(Arrays.asList(adminDTO));
        when(userService.convertToEntity(adminDTO)).thenReturn(admin);
        when(messageRepository.findChatIdsWithUser(1L)).thenReturn(Collections.emptyList());

        // When
        boolean result = chatService.canSendToAdmins(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void canSendToAdmins_shouldReturnFalse_whenNoAdminsAvailable() {
        // Given
        when(userService.findByRoleAndActiveIsTrue("ADMIN")).thenReturn(Collections.emptyList());

        // When
        boolean result = chatService.canSendToAdmins(1L);

        // Then
        assertThat(result).isFalse();
    }
}
