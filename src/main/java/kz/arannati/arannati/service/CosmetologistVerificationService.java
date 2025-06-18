package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.CosmetologistVerification;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CosmetologistVerificationService {

    Optional<CosmetologistVerification> findByUserId(Long userId);

    Page<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status);

    Page<CosmetologistVerification> findPendingVerifications(Pageable pageable);

    Page<CosmetologistVerification> findVerificationsByAdmin(Long adminId, Pageable pageable);

    boolean existsByUserIdAndStatus(Long userId, String status);

    long countByStatus(String status);

    long countByVerifiedBy(User verifiedBy);
    
    CosmetologistVerification save(CosmetologistVerification cosmetologistVerification);
    
    Optional<CosmetologistVerification> findById(Long id);
    
    void deleteById(Long id);
    
    List<CosmetologistVerification> findAll();
}