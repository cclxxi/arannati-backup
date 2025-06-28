package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;

import java.math.BigDecimal;

public interface PricingService {
    BigDecimal getEffectivePrice(ProductDTO product, UserDTO user);
    BigDecimal calculateDiscount(ProductDTO product, UserDTO user);
    boolean hasSpecialPrice(ProductDTO product, UserDTO user);
}
