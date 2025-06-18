package kz.arannati.arannati.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number cannot exceed 50 characters")
    private String orderNumber;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be a positive number")
    private BigDecimal totalAmount;

    @NotNull(message = "Discount amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be a positive number")
    private BigDecimal discountAmount;

    @NotNull(message = "Shipping amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping amount must be a positive number")
    private BigDecimal shippingAmount;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be a positive number")
    private BigDecimal taxAmount;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name cannot exceed 255 characters")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    private String customerEmail;

    @Size(max = 50, message = "Customer phone cannot exceed 50 characters")
    private String customerPhone;

    @NotBlank(message = "Delivery address is required")
    @Size(max = 1000, message = "Delivery address cannot exceed 1000 characters")
    private String deliveryAddress;

    @NotBlank(message = "Delivery method is required")
    @Size(max = 50, message = "Delivery method cannot exceed 50 characters")
    private String deliveryMethod;

    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method cannot exceed 50 characters")
    private String paymentMethod;

    @NotBlank(message = "Payment status is required")
    @Size(max = 50, message = "Payment status cannot exceed 50 characters")
    private String paymentStatus;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
