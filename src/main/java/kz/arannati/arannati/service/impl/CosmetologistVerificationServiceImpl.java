package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.CosmetologistVerification;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.CosmetologistVerificationRepository;
import kz.arannati.arannati.service.CosmetologistVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CosmetologistVerificationServiceImpl implements CosmetologistVerificationService {

    private final CosmetologistVerificationRepository cosmetologistVerificationRepository;

    @Autowired
    public CosmetologistVerificationServiceImpl(CosmetologistVerificationRepository cosmetologistVerificationRepository) {
        this.cosmetologistVerificationRepository = cosmetologistVerificationRepository;
    }

    @Override
    public Optional<CosmetologistVerification> findByUserId(Long userId) {
        return null;
    }

    @Override
    public Page<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable) {
        return null;
    }

    @Override
    public List<CosmetologistVerification> findByStatusOrderByCreatedAtDesc(String status) {
        return null;
    }

    @Override
    public Page<CosmetologistVerification> findPendingVerifications(Pageable pageable) {
        return null;
    }

    @Override
    public Page<CosmetologistVerification> findVerificationsByAdmin(Long adminId, Pageable pageable) {
        return null;
    }

    @Override
    public boolean existsByUserIdAndStatus(Long userId, String status) {
        return false;
    }

    @Override
    public long countByStatus(String status) {
        return 0;
    }

    @Override
    public long countByVerifiedBy(User verifiedBy) {
        return 0;
    }

    @Override
    public CosmetologistVerification save(CosmetologistVerification cosmetologistVerification) {
        return null;
    }

    @Override
    public Optional<CosmetologistVerification> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<CosmetologistVerification> findAll() {
        return null;
    }
}