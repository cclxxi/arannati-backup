package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.MessageRepository;
import kz.arannati.arannati.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private User recipient;
    private Message message;
    private UserDTO senderDTO;
    private UserDTO recipientDTO;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setFirstName("Sender");
        sender.setLastName("User");

        recipient = new User();
        recipient.setId(2L);
        recipient.setEmail("recipient@example.com");
        recipient.setFirstName("Recipient");
        recipient.setLastName("User");

        message = new Message();
        message.setId(1L);
        message.setContent("Test message");
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        senderDTO = new UserDTO();
        senderDTO.setId(1L);
        senderDTO.setEmail("sender@example.com");
        senderDTO.setFirstName("Sender");
        senderDTO.setLastName("User");

        recipientDTO = new UserDTO();
        recipientDTO.setId(2L);
        recipientDTO.setEmail("recipient@example.com");
        recipientDTO.setFirstName("Recipient");
        recipientDTO.setLastName("User");

        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setContent("Test message");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("Sender User");
        messageDTO.setSenderEmail("sender@example.com");
        messageDTO.setRecipientId(2L);
        messageDTO.setRecipientName("Recipient User");
        messageDTO.setRecipientEmail("recipient@example.com");
        messageDTO.setRead(false);
        messageDTO.setCreatedAt(message.getCreatedAt());
        messageDTO.setUpdatedAt(message.getUpdatedAt());
    }

    @Test
    void findById_shouldReturnMessage_whenMessageExists() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // When
        Optional<MessageDTO> result = messageService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getContent()).isEqualTo("Test message");
        assertThat(result.get().getSenderId()).isEqualTo(1L);
        assertThat(result.get().getRecipientId()).isEqualTo(2L);
    }

    @Test
    void findById_shouldReturnEmpty_whenMessageDoesNotExist() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<MessageDTO> result = messageService.findById(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findBySenderId_shouldReturnMessages_whenSenderExists() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(senderDTO));
        when(userService.convertToEntity(senderDTO)).thenReturn(sender);
        when(messageRepository.findBySenderOrderByCreatedAtDesc(sender))
                .thenReturn(Arrays.asList(message));

        // When
        List<MessageDTO> result = messageService.findBySenderId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent()).isEqualTo("Test message");
    }

    @Test
    void findBySenderId_shouldReturnEmptyList_whenSenderDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // When
        List<MessageDTO> result = messageService.findBySenderId(1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByRecipientId_shouldReturnMessages_whenRecipientExists() {
        // Given
        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.findByRecipientOrderByCreatedAtDesc(recipient))
                .thenReturn(Arrays.asList(message));

        // When
        List<MessageDTO> result = messageService.findByRecipientId(2L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent()).isEqualTo("Test message");
    }

    @Test
    void findByRecipientId_shouldReturnEmptyList_whenRecipientDoesNotExist() {
        // Given
        when(userService.findById(2L)).thenReturn(Optional.empty());

        // When
        List<MessageDTO> result = messageService.findByRecipientId(2L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findBySenderId_withPagination_shouldReturnPageOfMessages_whenSenderExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message), pageable, 1);

        when(userService.findById(1L)).thenReturn(Optional.of(senderDTO));
        when(userService.convertToEntity(senderDTO)).thenReturn(sender);
        when(messageRepository.findBySenderOrderByCreatedAtDesc(sender, pageable))
                .thenReturn(messagePage);

        // When
        Page<MessageDTO> result = messageService.findBySenderId(1L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findByRecipientId_withPagination_shouldReturnPageOfMessages_whenRecipientExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message), pageable, 1);

        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.findByRecipientOrderByCreatedAtDesc(recipient, pageable))
                .thenReturn(messagePage);

        // When
        Page<MessageDTO> result = messageService.findByRecipientId(2L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findUnreadByRecipientId_shouldReturnUnreadMessages_whenRecipientExists() {
        // Given
        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.findByRecipientAndReadIsFalseOrderByCreatedAtDesc(recipient))
                .thenReturn(Arrays.asList(message));

        // When
        List<MessageDTO> result = messageService.findUnreadByRecipientId(2L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).isRead()).isFalse();
    }

    @Test
    void countUnreadByRecipientId_shouldReturnCount_whenRecipientExists() {
        // Given
        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.countByRecipientAndReadIsFalse(recipient)).thenReturn(1L);

        // When
        long result = messageService.countUnreadByRecipientId(2L);

        // Then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void save_shouldSaveMessage() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(senderDTO));
        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(senderDTO)).thenReturn(sender);
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // When
        MessageDTO result = messageService.save(messageDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Test message");
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void markAsRead_shouldMarkMessageAsRead_whenMessageExists() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageRepository.save(message)).thenReturn(message);

        // When
        boolean result = messageService.markAsRead(1L);

        // Then
        assertThat(result).isTrue();
        assertThat(message.isRead()).isTrue();
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void markAsRead_shouldReturnFalse_whenMessageDoesNotExist() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = messageService.markAsRead(1L);

        // Then
        assertThat(result).isFalse();
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void deleteById_shouldDeleteMessage() {
        // When
        messageService.deleteById(1L);

        // Then
        verify(messageRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAll_shouldReturnAllMessages() {
        // Given
        when(messageRepository.findAll()).thenReturn(Arrays.asList(message));

        // When
        List<MessageDTO> result = messageService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void sendMessage_shouldSendMessage_whenUsersExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(senderDTO));
        when(userService.findById(2L)).thenReturn(Optional.of(recipientDTO));
        when(userService.convertToEntity(senderDTO)).thenReturn(sender);
        when(userService.convertToEntity(recipientDTO)).thenReturn(recipient);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // When
        MessageDTO result = messageService.sendMessage(1L, 2L, "Test message");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Test message");
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void sendMessage_shouldReturnNull_whenSenderDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // When
        MessageDTO result = messageService.sendMessage(1L, 2L, "Test message");

        // Then
        assertThat(result).isNull();
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_shouldReturnNull_whenRecipientDoesNotExist() {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(senderDTO));
        when(userService.findById(2L)).thenReturn(Optional.empty());

        // When
        MessageDTO result = messageService.sendMessage(1L, 2L, "Test message");

        // Then
        assertThat(result).isNull();
        verify(messageRepository, never()).save(any(Message.class));
    }
}