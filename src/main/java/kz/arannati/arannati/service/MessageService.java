package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    
    /**
     * Find a message by its ID
     * @param id the message ID
     * @return an Optional containing the message if found, or empty if not found
     */
    Optional<MessageDTO> findById(Long id);
    
    /**
     * Find messages sent by a user
     * @param userId the sender's user ID
     * @return a list of messages sent by the user
     */
    List<MessageDTO> findBySenderId(Long userId);
    
    /**
     * Find messages received by a user
     * @param userId the recipient's user ID
     * @return a list of messages received by the user
     */
    List<MessageDTO> findByRecipientId(Long userId);
    
    /**
     * Find messages sent by a user with pagination
     * @param userId the sender's user ID
     * @param pageable pagination information
     * @return a page of messages sent by the user
     */
    Page<MessageDTO> findBySenderId(Long userId, Pageable pageable);
    
    /**
     * Find messages received by a user with pagination
     * @param userId the recipient's user ID
     * @param pageable pagination information
     * @return a page of messages received by the user
     */
    Page<MessageDTO> findByRecipientId(Long userId, Pageable pageable);
    
    /**
     * Find unread messages received by a user
     * @param userId the recipient's user ID
     * @return a list of unread messages received by the user
     */
    List<MessageDTO> findUnreadByRecipientId(Long userId);
    
    /**
     * Count unread messages received by a user
     * @param userId the recipient's user ID
     * @return the number of unread messages
     */
    long countUnreadByRecipientId(Long userId);
    
    /**
     * Save a message
     * @param messageDTO the message to save
     * @return the saved message
     */
    MessageDTO save(MessageDTO messageDTO);
    
    /**
     * Mark a message as read
     * @param id the message ID
     * @return true if the message was marked as read, false otherwise
     */
    boolean markAsRead(Long id);
    
    /**
     * Delete a message by its ID
     * @param id the message ID
     */
    void deleteById(Long id);
    
    /**
     * Find all messages
     * @return a list of all messages
     */
    List<MessageDTO> findAll();
    
    /**
     * Convert a Message entity to a MessageDTO
     * @param message the Message entity
     * @return the MessageDTO
     */
    MessageDTO convertToDto(Message message);
    
    /**
     * Convert a MessageDTO to a Message entity
     * @param messageDTO the MessageDTO
     * @return the Message entity
     */
    Message convertToEntity(MessageDTO messageDTO);
    
    /**
     * Send a message from one user to another
     * @param senderId the sender's user ID
     * @param recipientId the recipient's user ID
     * @param content the message content
     * @return the sent message
     */
    MessageDTO sendMessage(Long senderId, Long recipientId, String content);
}