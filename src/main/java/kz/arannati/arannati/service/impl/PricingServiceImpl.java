package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    @Override
    public BigDecimal getEffectivePrice(ProductDTO product, UserDTO user) {
        if (user == null) {
            // Anonymous users see regular price or sale price
            return product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice();
        }

        String role = user.getRole();

        switch (role) {
            case "ADMIN":
                return product.getAdminPrice() != null ? product.getAdminPrice() :
                        (product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice());

            case "COSMETOLOGIST":
                return product.getCosmetologistPrice() != null ? product.getCosmetologistPrice() :
                        (product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice());

            default: // USER
                return product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice();
        }
    }

    @Override
    public BigDecimal calculateDiscount(ProductDTO product, UserDTO user) {
        BigDecimal regularPrice = product.getRegularPrice();
        BigDecimal effectivePrice = getEffectivePrice(product, user);

        if (regularPrice == null || effectivePrice == null || regularPrice.compareTo(effectivePrice) <= 0) {
            return BigDecimal.ZERO;
        }

        return regularPrice.subtract(effectivePrice);
    }

    @Override
    public boolean hasSpecialPrice(ProductDTO product, UserDTO user) {
        if (user == null) {
            return product.getSalePrice() != null;
        }

        String role = user.getRole();

        switch (role) {
            case "ADMIN":
                return product.getAdminPrice() != null || product.getSalePrice() != null;
            case "COSMETOLOGIST":
                return product.getCosmetologistPrice() != null || product.getSalePrice() != null;
            default:
                return product.getSalePrice() != null;
        }
    }
}