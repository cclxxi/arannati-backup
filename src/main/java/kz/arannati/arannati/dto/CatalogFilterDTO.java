package kz.arannati.arannati.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogFilterDTO {
    private List<Long> categoryIds;
    private List<Long> brandIds;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean onSale;
    private Boolean inStock;
    private String search;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}