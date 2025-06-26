package kz.arannati.arannati.service;

/**
 * Facade service for message operations to break circular dependency
 * between UserService and MessageService
 */
public interface MessageFacadeService {
    
    /**
     * Sends a message from one user to another
     * 
     * @param senderId ID of the sender
     * @param recipientId ID of the recipient
     * @param content Message content
     * @return true if message was sent successfully, false otherwise
     */
    boolean sendMessage(Long senderId, Long recipientId, String content);
}