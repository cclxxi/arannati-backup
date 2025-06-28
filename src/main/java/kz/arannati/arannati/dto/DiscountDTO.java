package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Long id;

    @NotBlank(message = "Discount name is required")
    @Size(max = 255, message = "Discount name cannot exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount percentage must be a positive number")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount percentage cannot exceed 100")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be a positive number")
    private BigDecimal discountAmount;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
