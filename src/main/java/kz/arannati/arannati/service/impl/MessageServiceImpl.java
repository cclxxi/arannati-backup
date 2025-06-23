package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.MessageRepository;
import kz.arannati.arannati.service.MessageService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Override
    public MessageDTO convertToDto(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .senderEmail(message.getSender().getEmail())
                .recipientId(message.getRecipient().getId())
                .recipientName(message.getRecipient().getFirstName() + " " + message.getRecipient().getLastName())
                .recipientEmail(message.getRecipient().getEmail())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    @Override
    public Message convertToEntity(MessageDTO messageDTO) {
        if (messageDTO == null) {
            return null;
        }

        Message message = new Message();
        message.setId(messageDTO.getId());
        message.setContent(messageDTO.getContent());
        message.setRead(messageDTO.isRead());
        message.setCreatedAt(messageDTO.getCreatedAt());
        message.setUpdatedAt(messageDTO.getUpdatedAt());

        // Set sender and recipient if IDs are provided
        if (messageDTO.getSenderId() != null) {
            Optional<UserDTO> senderDTO = userService.findById(messageDTO.getSenderId());
            if (senderDTO.isPresent()) {
                User sender = userService.convertToEntity(senderDTO.get());
                message.setSender(sender);
            }
        }

        if (messageDTO.getRecipientId() != null) {
            Optional<UserDTO> recipientDTO = userService.findById(messageDTO.getRecipientId());
            if (recipientDTO.isPresent()) {
                User recipient = userService.convertToEntity(recipientDTO.get());
                message.setRecipient(recipient);
            }
        }

        return message;
    }

    @Override
    public Optional<MessageDTO> findById(Long id) {
        return messageRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public List<MessageDTO> findBySenderId(Long userId) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return List.of();
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        return messageRepository.findBySenderOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> findByRecipientId(Long userId) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return List.of();
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        return messageRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageDTO> findBySenderId(Long userId, Pageable pageable) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return Page.empty();
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        Page<Message> messagePage = messageRepository.findBySenderOrderByCreatedAtDesc(user, pageable);
        List<MessageDTO> messageDTOs = messagePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(messageDTOs, pageable, messagePage.getTotalElements());
    }

    @Override
    public Page<MessageDTO> findByRecipientId(Long userId, Pageable pageable) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return Page.empty();
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        Page<Message> messagePage = messageRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
        List<MessageDTO> messageDTOs = messagePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(messageDTOs, pageable, messagePage.getTotalElements());
    }

    @Override
    public List<MessageDTO> findUnreadByRecipientId(Long userId) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return List.of();
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        return messageRepository.findByRecipientAndIsReadIsFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadByRecipientId(Long userId) {
        Optional<UserDTO> userDtoOpt = userService.findById(userId);
        if (userDtoOpt.isEmpty()) {
            return 0;
        }

        User user = userService.convertToEntity(userDtoOpt.get());
        return messageRepository.countByRecipientAndIsReadIsFalse(user);
    }

    @Override
    public MessageDTO save(MessageDTO messageDTO) {
        Message message = convertToEntity(messageDTO);
        Message savedMessage = messageRepository.save(message);
        return convertToDto(savedMessage);
    }

    @Override
    public boolean markAsRead(Long id) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            return false;
        }

        Message message = messageOpt.get();
        message.setRead(true);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
        return true;
    }

    @Override
    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public List<MessageDTO> findAll() {
        return messageRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDTO sendMessage(Long senderId, Long recipientId, String content) {
        Optional<UserDTO> senderDtoOpt = userService.findById(senderId);
        Optional<UserDTO> recipientDtoOpt = userService.findById(recipientId);

        if (senderDtoOpt.isEmpty() || recipientDtoOpt.isEmpty()) {
            log.error("Failed to send message: sender or recipient not found");
            return null;
        }

        User sender = userService.convertToEntity(senderDtoOpt.get());
        User recipient = userService.convertToEntity(recipientDtoOpt.get());

        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);
        log.info("Message sent from {} to {}", sender.getEmail(), recipient.getEmail());

        return convertToDto(savedMessage);
    }
}
