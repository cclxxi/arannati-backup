package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CosmetologistVerificationDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Diploma file path is required")
    @Size(max = 255, message = "Diploma file path cannot exceed 255 characters")
    private String diplomaFilePath;

    @NotBlank(message = "Diploma original filename is required")
    @Size(max = 255, message = "Diploma original filename cannot exceed 255 characters")
    private String diplomaOriginalFilename;

    @Size(max = 255, message = "Institution name cannot exceed 255 characters")
    private String institutionName;

    @Min(value = 1900, message = "Graduation year must be after 1900")
    @Max(value = 2100, message = "Graduation year must be before 2100")
    private Integer graduationYear;

    @Size(max = 255, message = "Specialization cannot exceed 255 characters")
    private String specialization;

    @Size(max = 100, message = "License number cannot exceed 100 characters")
    private String licenseNumber;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    @Size(max = 1000, message = "Admin comment cannot exceed 1000 characters")
    private String adminComment;

    @Size(max = 1000, message = "Rejection reason cannot exceed 1000 characters")
    private String rejectionReason;

    private Long verifiedById;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;
}
