package kz.arannati.arannati.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemWithProductDTO {
    private WishlistDTO wishlistItem;
    private ProductDTO product;
}