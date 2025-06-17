package kz.arannati.arannati.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cosmetologist_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CosmetologistVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "diploma_file_path", nullable = false)
    private String diplomaFilePath;

    @Column(name = "diploma_original_filename", nullable = false)
    private String diplomaOriginalFilename;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    private String specialization;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(nullable = false)
    private String status;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}