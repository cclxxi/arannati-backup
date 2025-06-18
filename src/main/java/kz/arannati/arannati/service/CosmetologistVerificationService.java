package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.CosmetologistVerificationDTO;
import kz.arannati.arannati.entity.CosmetologistVerification;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CosmetologistVerificationService {

    Optional<CosmetologistVerificationDTO> findByUserId(Long userId);

    Page<CosmetologistVerificationDTO> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<CosmetologistVerificationDTO> findByStatusOrderByCreatedAtDesc(String status);

    Page<CosmetologistVerificationDTO> findPendingVerifications(Pageable pageable);

    Page<CosmetologistVerificationDTO> findVerificationsByAdmin(Long adminId, Pageable pageable);

    boolean existsByUserIdAndStatus(Long userId, String status);

    long countByStatus(String status);

    long countByVerifiedBy(User verifiedBy);

    CosmetologistVerificationDTO save(CosmetologistVerificationDTO cosmetologistVerificationDTO);

    Optional<CosmetologistVerificationDTO> findById(Long id);

    void deleteById(Long id);

    List<CosmetologistVerificationDTO> findAll();

    CosmetologistVerificationDTO convertToDto(CosmetologistVerification cosmetologistVerification);

    CosmetologistVerification convertToEntity(CosmetologistVerificationDTO cosmetologistVerificationDTO);
}
