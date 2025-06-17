package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
    private Long id;

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Logo path cannot exceed 255 characters")
    private String logoPath;

    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    private String country;

    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Website URL must be a valid URL")
    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    private String websiteUrl;

    private boolean isActive;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
