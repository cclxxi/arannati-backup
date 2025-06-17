package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Long parentId;

    private List<Long> childrenIds = new ArrayList<>();

    @Size(max = 255, message = "Image path cannot exceed 255 characters")
    private String imagePath;

    private boolean isActive;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
