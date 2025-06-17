package kz.arannati.arannati.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private String previewText;
    private String imagePath;
    private Long authorId;
    private boolean isPublished;
    private boolean isFeatured;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private Integer sortOrder;
    private Integer viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}