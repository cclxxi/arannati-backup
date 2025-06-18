package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadableFileDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "File path is required")
    @Size(max = 255, message = "File path cannot exceed 255 characters")
    private String filePath;

    @NotBlank(message = "Original filename is required")
    @Size(max = 255, message = "Original filename cannot exceed 255 characters")
    private String originalFilename;

    @NotNull(message = "File size is required")
    @Min(value = 0, message = "File size must be a positive number")
    private Long fileSize;

    @NotBlank(message = "MIME type is required")
    @Size(max = 100, message = "MIME type cannot exceed 100 characters")
    private String mimeType;

    @NotBlank(message = "Access level is required")
    @Size(max = 50, message = "Access level cannot exceed 50 characters")
    private String accessLevel;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    private boolean isActive;

    @NotNull(message = "Download count is required")
    @Min(value = 0, message = "Download count must be a positive number")
    private Integer downloadCount;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    @NotNull(message = "Uploaded by ID is required")
    private Long uploadedById;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
