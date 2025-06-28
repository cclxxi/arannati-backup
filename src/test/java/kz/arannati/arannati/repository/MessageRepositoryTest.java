package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.enums.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private User sender;
    private User recipient;
    private Message message1;
    private Message message2;
    private Message message3;

    @BeforeEach
    void setUp() {
        // Create test users
        sender = new User();
        sender.setEmail("sender@example.com");
        sender.setFirstName("Sender");
        sender.setLastName("User");
        sender.setPassword("password");
        entityManager.persist(sender);

        recipient = new User();
        recipient.setEmail("recipient@example.com");
        recipient.setFirstName("Recipient");
        recipient.setLastName("User");
        recipient.setPassword("password");
        entityManager.persist(recipient);

        // Create test messages
        message1 = Message.builder()
                .content("Test message 1")
                .sender(sender)
                .recipient(recipient)
                .read(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .messageType(MessageType.DIRECT)
                .isBroadcast(false)
                .isSystemMessage(false)
                .build();
        entityManager.persist(message1);

        message2 = Message.builder()
                .content("Test message 2")
                .sender(recipient)
                .recipient(sender)
                .read(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .messageType(MessageType.DIRECT)
                .isBroadcast(false)
                .isSystemMessage(false)
                .build();
        entityManager.persist(message2);

        message3 = Message.builder()
                .content("Test message 3")
                .sender(sender)
                .recipient(recipient)
                .read(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .messageType(MessageType.DIRECT)
                .isBroadcast(false)
                .isSystemMessage(false)
                .build();
        entityManager.persist(message3);

        entityManager.flush();
    }

    @Test
    void findBySenderOrderByCreatedAtDesc() {
        // When
        List<Message> messages = messageRepository.findBySenderOrderByCreatedAtDesc(sender);

        // Then
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getId()).isEqualTo(message3.getId());
        assertThat(messages.get(1).getId()).isEqualTo(message1.getId());
    }

    @Test
    void findByRecipientOrderByCreatedAtDesc() {
        // When
        List<Message> messages = messageRepository.findByRecipientOrderByCreatedAtDesc(recipient);

        // Then
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getId()).isEqualTo(message3.getId());
        assertThat(messages.get(1).getId()).isEqualTo(message1.getId());
    }

    @Test
    void findBySenderOrderByCreatedAtDescWithPagination() {
        // When
        Page<Message> messagePage = messageRepository.findBySenderOrderByCreatedAtDesc(sender, PageRequest.of(0, 1));

        // Then
        assertThat(messagePage.getContent()).hasSize(1);
        assertThat(messagePage.getTotalElements()).isEqualTo(2);
        assertThat(messagePage.getContent().get(0).getId()).isEqualTo(message3.getId());
    }

    @Test
    void findByRecipientOrderByCreatedAtDescWithPagination() {
        // When
        Page<Message> messagePage = messageRepository.findByRecipientOrderByCreatedAtDesc(recipient, PageRequest.of(0, 1));

        // Then
        assertThat(messagePage.getContent()).hasSize(1);
        assertThat(messagePage.getTotalElements()).isEqualTo(2);
        assertThat(messagePage.getContent().get(0).getId()).isEqualTo(message3.getId());
    }

    @Test
    void findByRecipientAndReadIsFalseOrderByCreatedAtDesc() {
        // When
        List<Message> messages = messageRepository.findByRecipientAndReadIsFalseOrderByCreatedAtDesc(recipient);

        // Then
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getId()).isEqualTo(message3.getId());
        assertThat(messages.get(1).getId()).isEqualTo(message1.getId());
    }

    @Test
    void countByRecipientAndReadIsFalse() {
        // When
        long count = messageRepository.countByRecipientAndReadIsFalse(recipient);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void findUserMessages() {
        // When
        List<Message> messages = messageRepository.findUserMessages(sender.getId());

        // Then
        assertThat(messages).hasSize(3);
    }

    @Test
    void findByChatIdOrderByCreatedAtAsc() {
        // Given
        String chatId = message1.getChatId();

        // When
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);

        // Then
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getId()).isEqualTo(message1.getId());
        assertThat(messages.get(1).getId()).isEqualTo(message2.getId());
        assertThat(messages.get(2).getId()).isEqualTo(message3.getId());
    }

    @Test
    void findUnreadInChat() {
        // Given
        String chatId = message1.getChatId();

        // When
        List<Message> messages = messageRepository.findUnreadInChat(chatId, recipient.getId());

        // Then
        assertThat(messages).hasSize(2);
        assertThat(messages).extracting(Message::getId).containsExactlyInAnyOrder(message1.getId(), message3.getId());
    }

    @Test
    void countUnreadByRecipientId() {
        // When
        long count = messageRepository.countUnreadByRecipientId(recipient.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void findChatIdsWithUser() {
        // When
        List<String> chatIds = messageRepository.findChatIdsWithUser(sender.getId());

        // Then
        assertThat(chatIds).hasSize(1);
        assertThat(chatIds.get(0)).isEqualTo(message1.getChatId());
    }
}