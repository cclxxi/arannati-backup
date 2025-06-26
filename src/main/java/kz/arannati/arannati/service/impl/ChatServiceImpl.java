package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.enums.MessageType;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.MessageRepository;
import kz.arannati.arannati.service.ChatService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Override
    @Transactional
    public MessageDTO sendDirectMessage(Long senderId, Long recipientId, String content) {
        User sender = userService.findById(senderId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userService.findById(recipientId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .recipient(recipient)
                .messageType(MessageType.DIRECT)
                .read(false)
                .isBroadcast(false)
                .isSystemMessage(false)
                .build();

        message = messageRepository.save(message);
        log.info("Direct message sent from {} to {}", sender.getEmail(), recipient.getEmail());

        return convertToDto(message);
    }

    @Override
    @Transactional
    public MessageDTO sendSupportRequest(Long senderId, String content) {
        User sender = userService.findById(senderId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        List<Long> availableAdminIds = getAvailableAdminsForUser(senderId);

        if (availableAdminIds.isEmpty()) {
            throw new RuntimeException("No available admins to handle the request");
        }

        // Создаем сообщения-рассылки для всех доступных админов
        List<Message> broadcastMessages = new ArrayList<>();

        for (Long adminId : availableAdminIds) {
            User admin = userService.findById(adminId)
                    .map(userService::convertToEntity)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            Message message = Message.builder()
                    .content(content)
                    .sender(sender)
                    .recipient(admin)
                    .messageType(MessageType.SUPPORT_REQUEST)
                    .read(false)
                    .isBroadcast(true)
                    .isSystemMessage(false)
                    .build();

            broadcastMessages.add(message);
        }

        messageRepository.saveAll(broadcastMessages);

        log.info("Support request sent from {} to {} admins",
                sender.getEmail(), availableAdminIds.size());

        // Возвращаем первое сообщение как представитель
        return convertToDto(broadcastMessages.get(0));
    }

    @Override
    @Transactional
    public MessageDTO replyToSupportRequest(Long adminId, Long originalMessageId, String content) {
        Message originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new RuntimeException("Original message not found"));

        if (!originalMessage.isBroadcast()) {
            throw new RuntimeException("Original message is not a broadcast");
        }

        User admin = userService.findById(adminId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        User user = originalMessage.getSender();

        // Создаем ответное сообщение как прямое
        Message replyMessage = Message.builder()
                .content(content)
                .sender(admin)
                .recipient(user)
                .messageType(MessageType.DIRECT)
                .read(false)
                .isBroadcast(false)
                .isSystemMessage(false)
                .originalMessageId(originalMessageId)
                .build();

        replyMessage = messageRepository.save(replyMessage);

        // Отмечаем оригинальное сообщение как отвеченное
        originalMessage.setBroadcastRespondedBy(adminId);
        messageRepository.save(originalMessage);

        // Удаляем рассылки у других админов
        removeBroadcastsAfterResponse(originalMessageId, adminId);

        log.info("Admin {} replied to support request from {}",
                admin.getEmail(), user.getEmail());

        return convertToDto(replyMessage);
    }

    @Override
    @Transactional
    public MessageDTO sendDeclineMessage(Long adminId, Long cosmetologistId, String reason) {
        User admin = userService.findById(adminId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        User cosmetologist = userService.findById(cosmetologistId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("Cosmetologist not found"));

        String systemContent = "Ваша заявка на верификацию косметолога была отклонена.\n\n" +
                "Причина отклонения: " + reason + "\n\n" +
                "Вы можете исправить указанные недостатки и подать заявку повторно.";

        Message declineMessage = Message.builder()
                .content(systemContent)
                .sender(admin)
                .recipient(cosmetologist)
                .messageType(MessageType.SYSTEM_DECLINE)
                .read(false)
                .isBroadcast(false)
                .isSystemMessage(true)
                .build();

        declineMessage = messageRepository.save(declineMessage);

        log.info("Decline message sent from admin {} to cosmetologist {}",
                admin.getEmail(), cosmetologist.getEmail());

        return convertToDto(declineMessage);
    }

    @Override
    public List<ChatDTO> getUserChats(Long userId) {
        User user = userService.findById(userId)
                .map(userService::convertToEntity)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем все сообщения пользователя (отправленные и полученные)
        List<Message> allMessages = messageRepository.findUserMessages(userId);

        // Группируем по chatId
        Map<String, List<Message>> chatGroups = allMessages.stream()
                .filter(m -> !m.isBroadcast() || m.getBroadcastRespondedBy() != null)
                .collect(Collectors.groupingBy(Message::getChatId));

        List<ChatDTO> chats = new ArrayList<>();

        for (Map.Entry<String, List<Message>> entry : chatGroups.entrySet()) {
            String chatId = entry.getKey();
            List<Message> messages = entry.getValue();

            // Сортируем сообщения по дате
            messages.sort(Comparator.comparing(Message::getCreatedAt).reversed());
            Message lastMessage = messages.get(0);

            // Определяем собеседника
            User otherUser = lastMessage.getSender().getId().equals(userId)
                    ? lastMessage.getRecipient()
                    : lastMessage.getSender();

            // Подсчитываем непрочитанные сообщения
            long unreadCount = messages.stream()
                    .filter(m -> m.getRecipient().getId().equals(userId) && !m.isRead())
                    .count();

            ChatDTO chatDto = ChatDTO.builder()
                    .chatId(chatId)
                    .otherUserId(otherUser.getId())
                    .otherUserName(otherUser.getFirstName() + " " + otherUser.getLastName())
                    .otherUserEmail(otherUser.getEmail())
                    .otherUserRole(otherUser.getRole().getName())
                    .otherUserVerified(otherUser.isVerified())
                    .lastMessage(lastMessage.getContent())
                    .lastMessageTime(lastMessage.getCreatedAt())
                    .lastMessageFromMe(lastMessage.getSender().getId().equals(userId))
                    .hasUnreadMessages(unreadCount > 0)
                    .unreadCount(unreadCount)
                    .chatType(ChatDTO.determineChatType(
                            user.getRole().getName(),
                            otherUser.getRole().getName()))
                    .isSystemChat(lastMessage.isSystemMessage())
                    .build();

            chats.add(chatDto);
        }

        // Сортируем чаты по времени последнего сообщения
        chats.sort(Comparator.comparing(ChatDTO::getLastMessageTime).reversed());

        return chats;
    }

    @Override
    public List<MessageDTO> getChatMessages(String chatId, Long userId) {
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);

        return messages.stream()
                .filter(m -> m.canUserRead(userId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getActiveSupportRequests() {
        List<Message> broadcastMessages = messageRepository.findActiveBroadcasts();

        return broadcastMessages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadMessagesCount(Long userId) {
        return messageRepository.countUnreadByRecipientId(userId);
    }

    @Override
    @Transactional
    public void markChatAsRead(String chatId, Long userId) {
        List<Message> unreadMessages = messageRepository.findUnreadInChat(chatId, userId);

        for (Message message : unreadMessages) {
            message.setRead(true);
        }

        messageRepository.saveAll(unreadMessages);

        log.info("Marked {} messages as read in chat {} for user {}",
                unreadMessages.size(), chatId, userId);
    }

    @Override
    public boolean canSendToAdmins(Long userId) {
        List<Long> availableAdmins = getAvailableAdminsForUser(userId);
        return !availableAdmins.isEmpty();
    }

    @Override
    public List<Long> getAvailableAdminsForUser(Long userId) {
        List<User> allAdmins = userService.findByRoleAndActiveIsTrue("ADMIN").stream()
                .map(userService::convertToEntity)
                .collect(Collectors.toList());
        List<String> existingChatIds = messageRepository.findChatIdsWithUser(userId);

        return allAdmins.stream()
                .filter(admin -> {
                    String chatId = generateChatId(userId, admin.getId());
                    return !existingChatIds.contains(chatId);
                })
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeBroadcastsAfterResponse(Long originalMessageId, Long respondingAdminId) {
        Message originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new RuntimeException("Original message not found"));

        // Находим все сообщения-рассылки с тем же содержимым и отправителем
        List<Message> relatedBroadcasts = messageRepository.findRelatedBroadcasts(
                originalMessage.getSender().getId(),
                originalMessage.getContent(),
                originalMessage.getCreatedAt()
        );

        // Удаляем рассылки у других админов
        List<Message> toDelete = relatedBroadcasts.stream()
                .filter(m -> !m.getRecipient().getId().equals(respondingAdminId))
                .collect(Collectors.toList());

        messageRepository.deleteAll(toDelete);

        log.info("Removed {} broadcast messages after admin {} responded",
                toDelete.size(), respondingAdminId);
    }

    private MessageDTO convertToDto(Message message) {
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
                .chatId(message.getChatId())
                .messageType(message.getMessageType().name())
                .isBroadcast(message.isBroadcast())
                .isSystemMessage(message.isSystemMessage())
                .build();
    }

    private String generateChatId(Long userId1, Long userId2) {
        long smaller = Math.min(userId1, userId2);
        long larger = Math.max(userId1, userId2);
        return "chat_" + smaller + "_" + larger;
    }
}
