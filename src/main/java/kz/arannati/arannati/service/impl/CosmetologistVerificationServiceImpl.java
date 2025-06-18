package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.CosmetologistVerificationDTO;
import kz.arannati.arannati.entity.CosmetologistVerification;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.CosmetologistVerificationRepository;
import kz.arannati.arannati.service.CosmetologistVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CosmetologistVerificationServiceImpl implements CosmetologistVerificationService {

    private final CosmetologistVerificationRepository cosmetologistVerificationRepository;

    @Autowired
    public CosmetologistVerificationServiceImpl(CosmetologistVerificationRepository cosmetologistVerificationRepository) {
        this.cosmetologistVerificationRepository = cosmetologistVerificationRepository;
    }

    @Override
    public CosmetologistVerificationDTO convertToDto(CosmetologistVerification cosmetologistVerification) {
        if (cosmetologistVerification == null) {
            return null;
        }

        CosmetologistVerificationDTO.CosmetologistVerificationDTOBuilder builder = CosmetologistVerificationDTO.builder()
                .id(cosmetologistVerification.getId())
                .diplomaFilePath(cosmetologistVerification.getDiplomaFilePath())
                .diplomaOriginalFilename(cosmetologistVerification.getDiplomaOriginalFilename())
                .institutionName(cosmetologistVerification.getInstitutionName())
                .graduationYear(cosmetologistVerification.getGraduationYear())
                .specialization(cosmetologistVerification.getSpecialization())
                .licenseNumber(cosmetologistVerification.getLicenseNumber())
                .status(cosmetologistVerification.getStatus())
                .adminComment(cosmetologistVerification.getAdminComment())
                .rejectionReason(cosmetologistVerification.getRejectionReason())
                .createdAt(cosmetologistVerification.getCreatedAt())
                .updatedAt(cosmetologistVerification.getUpdatedAt())
                .verifiedAt(cosmetologistVerification.getVerifiedAt());

        if (cosmetologistVerification.getUser() != null) {
            builder.userId(cosmetologistVerification.getUser().getId());
        }

        if (cosmetologistVerification.getVerifiedBy() != null) {
            builder.verifiedById(cosmetologistVerification.getVerifiedBy().getId());
        }

        return builder.build();
    }

    @Override
    public CosmetologistVerification convertToEntity(CosmetologistVerificationDTO cosmetologistVerificationDTO) {
        if (cosmetologistVerificationDTO == null) {
            return null;
        }

        CosmetologistVerification cosmetologistVerification = new CosmetologistVerification();
        cosmetologistVerification.setId(cosmetologistVerificationDTO.getId());
        cosmetologistVerification.setDiplomaFilePath(cosmetologistVerificationDTO.getDiplomaFilePath());
        cosmetologistVerification.setDiplomaOriginalFilename(cosmetologistVerificationDTO.getDiplomaOriginalFilename());
        cosmetologistVerification.setInstitutionName(cosmetologistVerificationDTO.getInstitutionName());
        cosmetologistVerification.setGraduationYear(cosmetologistVerificationDTO.getGraduationYear());
        cosmetologistVerification.setSpecialization(cosmetologistVerificationDTO.getSpecialization());
        cosmetologistVerification.setLicenseNumber(cosmetologistVerificationDTO.getLicenseNumber());
        cosmetologistVerification.setStatus(cosmetologistVerificationDTO.getStatus());
        cosmetologistVerification.setAdminComment(cosmetologistVerificationDTO.getAdminComment());
        cosmetologistVerification.setRejectionReason(cosmetologistVerificationDTO.getRejectionReason());
        cosmetologistVerification.setCreatedAt(cosmetologistVerificationDTO.getCreatedAt());
        cosmetologistVerification.setUpdatedAt(cosmetologistVerificationDTO.getUpdatedAt());
        cosmetologistVerification.setVerifiedAt(cosmetologistVerificationDTO.getVerifiedAt());

        // User and VerifiedBy relationships are typically handled separately
        // as they require fetching the actual entities from the database

        return cosmetologistVerification;
    }

    @Override
    public Optional<CosmetologistVerificationDTO> findByUserId(Long userId) {
        return cosmetologistVerificationRepository.findByUserId(userId)
                .map(this::convertToDto);
    }

    @Override
    public Page<CosmetologistVerificationDTO> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable) {
        Page<CosmetologistVerification> verificationPage = cosmetologistVerificationRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        List<CosmetologistVerificationDTO> verificationDTOs = verificationPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(verificationDTOs, pageable, verificationPage.getTotalElements());
    }

    @Override
    public List<CosmetologistVerificationDTO> findByStatusOrderByCreatedAtDesc(String status) {
        return cosmetologistVerificationRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CosmetologistVerificationDTO> findPendingVerifications(Pageable pageable) {
        Page<CosmetologistVerification> verificationPage = cosmetologistVerificationRepository.findPendingVerifications(pageable);
        List<CosmetologistVerificationDTO> verificationDTOs = verificationPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(verificationDTOs, pageable, verificationPage.getTotalElements());
    }

    @Override
    public Page<CosmetologistVerificationDTO> findVerificationsByAdmin(Long adminId, Pageable pageable) {
        Page<CosmetologistVerification> verificationPage = cosmetologistVerificationRepository.findVerificationsByAdmin(adminId, pageable);
        List<CosmetologistVerificationDTO> verificationDTOs = verificationPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(verificationDTOs, pageable, verificationPage.getTotalElements());
    }

    @Override
    public boolean existsByUserIdAndStatus(Long userId, String status) {
        return cosmetologistVerificationRepository.existsByUserIdAndStatus(userId, status);
    }

    @Override
    public long countByStatus(String status) {
        return cosmetologistVerificationRepository.countByStatus(status);
    }

    @Override
    public long countByVerifiedBy(User verifiedBy) {
        return cosmetologistVerificationRepository.countByVerifiedBy(verifiedBy);
    }

    @Override
    public CosmetologistVerificationDTO save(CosmetologistVerificationDTO cosmetologistVerificationDTO) {
        CosmetologistVerification cosmetologistVerification = convertToEntity(cosmetologistVerificationDTO);

        // Handle user relationship if userId is provided
        if (cosmetologistVerificationDTO.getUserId() != null) {
            User user = new User();
            user.setId(cosmetologistVerificationDTO.getUserId());
            cosmetologistVerification.setUser(user);
        }

        // Handle verifiedBy relationship if verifiedById is provided
        if (cosmetologistVerificationDTO.getVerifiedById() != null) {
            User verifiedBy = new User();
            verifiedBy.setId(cosmetologistVerificationDTO.getVerifiedById());
            cosmetologistVerification.setVerifiedBy(verifiedBy);
        }

        CosmetologistVerification savedVerification = cosmetologistVerificationRepository.save(cosmetologistVerification);
        return convertToDto(savedVerification);
    }

    @Override
    public Optional<CosmetologistVerificationDTO> findById(Long id) {
        return cosmetologistVerificationRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        cosmetologistVerificationRepository.deleteById(id);
    }

    @Override
    public List<CosmetologistVerificationDTO> findAll() {
        return cosmetologistVerificationRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}