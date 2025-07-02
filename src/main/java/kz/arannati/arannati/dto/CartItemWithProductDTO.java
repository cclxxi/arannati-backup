package kz.arannati.arannati.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemWithProductDTO {
    private CartDTO cartItem;
    private ProductDTO product;
    private BigDecimal effectivePrice;
    private BigDecimal itemTotal;
}