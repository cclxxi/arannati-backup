package kz.arannati.arannati.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadableFileDTO {
    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String originalFilename;
    private Long fileSize;
    private String mimeType;
    private String accessLevel;
    private String category;
    private boolean isActive;
    private Integer downloadCount;
    private Integer sortOrder;
    private Long uploadedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}