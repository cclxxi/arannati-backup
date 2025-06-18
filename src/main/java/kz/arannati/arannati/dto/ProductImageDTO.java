package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Image path is required")
    @Size(max = 255, message = "Image path cannot exceed 255 characters")
    private String imagePath;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    private String altText;

    private boolean isMain;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    private LocalDateTime createdAt;
}
