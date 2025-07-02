package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    @Size(max = 20, message = "Customer phone cannot exceed 20 characters")
    private String customerPhone;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 500, message = "Delivery address cannot exceed 500 characters")
    private String deliveryAddress;

    @NotBlank(message = "Delivery method is required")
    @Size(max = 100, message = "Delivery method cannot exceed 100 characters")
    private String deliveryMethod;

    @NotBlank(message = "Payment method is required")
    @Size(max = 100, message = "Payment method cannot exceed 100 characters")
    private String paymentMethod;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemCreateDTO> items;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be a positive number")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping amount must be a positive number")
    private BigDecimal shippingAmount;
}
