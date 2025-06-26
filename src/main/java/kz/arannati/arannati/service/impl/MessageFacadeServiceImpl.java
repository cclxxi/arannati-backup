package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.MessageRepository;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.MessageFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of MessageFacadeService that directly uses repositories
 * to avoid circular dependencies
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageFacadeServiceImpl implements MessageFacadeService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public boolean sendMessage(Long senderId, Long recipientId, String content) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            log.error("Failed to send message: sender or recipient not found");
            return false;
        }

        User sender = senderOpt.get();
        User recipient = recipientOpt.get();

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);
        log.info("Message sent from {} to {}", sender.getEmail(), recipient.getEmail());

        return true;
    }
}