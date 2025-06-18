package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;

    @Size(max = 500, message = "Preview text cannot exceed 500 characters")
    private String previewText;

    @Size(max = 255, message = "Image path cannot exceed 255 characters")
    private String imagePath;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    private boolean isPublished;

    private boolean isFeatured;

    @Size(max = 255, message = "Slug cannot exceed 255 characters")
    private String slug;

    @Size(max = 255, message = "Meta title cannot exceed 255 characters")
    private String metaTitle;

    @Size(max = 500, message = "Meta description cannot exceed 500 characters")
    private String metaDescription;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    @NotNull(message = "Views count is required")
    @Min(value = 0, message = "Views count must be a positive number")
    private Integer viewsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;
}
