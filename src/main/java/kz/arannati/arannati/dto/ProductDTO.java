package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Size(max = 1000, message = "Short description cannot exceed 1000 characters")
    private String shortDescription;

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    private String sku;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Regular price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Regular price must be greater than 0")
    private BigDecimal regularPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cosmetologist price must be greater than 0")
    private BigDecimal cosmetologistPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
    private BigDecimal salePrice;

    private boolean isProfessional;

    private boolean isActive;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be a positive number")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be a positive number")
    private BigDecimal weight;

    @Size(max = 100, message = "Dimensions cannot exceed 100 characters")
    private String dimensions;

    @Size(max = 5000, message = "Ingredients cannot exceed 5000 characters")
    private String ingredients;

    @Size(max = 5000, message = "Usage instructions cannot exceed 5000 characters")
    private String usageInstructions;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be a positive number")
    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
