package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Message;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
    
    List<Message> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    Page<Message> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
    
    Page<Message> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    List<Message> findByRecipientAndIsReadIsFalseOrderByCreatedAtDesc(User recipient);
    
    long countByRecipientAndIsReadIsFalse(User recipient);
}