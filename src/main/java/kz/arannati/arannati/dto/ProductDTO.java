package kz.arannati.arannati.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private String sku;
    private Long categoryId;
    private Long brandId;
    private BigDecimal regularPrice;
    private BigDecimal cosmetologistPrice;
    private BigDecimal salePrice;
    private boolean isProfessional;
    private boolean isActive;
    private Integer stockQuantity;
    private BigDecimal weight;
    private String dimensions;
    private String ingredients;
    private String usageInstructions;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}