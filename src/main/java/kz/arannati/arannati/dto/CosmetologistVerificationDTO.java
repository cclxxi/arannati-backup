package kz.arannati.arannati.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CosmetologistVerificationDTO {
    private Long id;
    private Long userId;
    private String diplomaFilePath;
    private String diplomaOriginalFilename;
    private String institutionName;
    private Integer graduationYear;
    private String specialization;
    private String licenseNumber;
    private String status;
    private String adminComment;
    private String rejectionReason;
    private Long verifiedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime verifiedAt;
}