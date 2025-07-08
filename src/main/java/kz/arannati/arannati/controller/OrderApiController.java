package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API controller for order functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController extends BaseApiController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final PricingService pricingService;
    private final ProductService productService;
    private final NotificationService notificationService;

    /**
     * Get order summary for checkout
     * @return Order summary including cart items, total amount, shipping amount, and final amount
     */
    @GetMapping("/checkout")
    public ResponseEntity<Map<String, Object>> getCheckoutSummary() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get user
            UserDTO user = userService.findById(userId).orElse(null);
            if (user == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get cart items
            List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(userId);
            if (cartItems.isEmpty()) {
                return errorResponse("Cart is empty", HttpStatus.BAD_REQUEST);
            }

            // Enrich cart items with product details
            List<CartItemWithProductDTO> cartItemsWithProducts = cartItems.stream()
                    .map(cartItem -> enrichCartItemWithProduct(cartItem, user))
                    .filter(item -> item != null)
                    .collect(Collectors.toList());

            // Calculate totals
            BigDecimal totalAmount = calculateCartTotal(cartItemsWithProducts);
            BigDecimal shippingAmount = calculateShipping(totalAmount);
            BigDecimal finalAmount = totalAmount.add(shippingAmount);

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("cartItems", cartItemsWithProducts);
            data.put("totalAmount", totalAmount);
            data.put("shippingAmount", shippingAmount);
            data.put("finalAmount", finalAmount);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting checkout summary: {}", e.getMessage());
            return errorResponse("Failed to get checkout summary: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new order
     * @param orderCreateDTO Order creation data
     * @return Created order details
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Validate cart is not empty
            List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(userId);
            if (cartItems.isEmpty()) {
                return errorResponse("Cart is empty", HttpStatus.BAD_REQUEST);
            }

            // Create order
            OrderDTO order = orderService.createOrder(orderCreateDTO, userId);

            // Clear cart after successful order
            cartService.deleteAllByUserId(userId);

            // Send notification to admins
            notificationService.notifyNewOrder(order.getId());

            log.info("Order created successfully: {}", order.getOrderNumber());

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getId());
            data.put("orderNumber", order.getOrderNumber());
            
            return successResponse(data, "Order created successfully");
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return errorResponse("Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get user's orders with pagination
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of orders
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get orders
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(userId, pageable);

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("orders", orders.getContent());
            data.put("currentPage", orders.getNumber());
            data.put("totalItems", orders.getTotalElements());
            data.put("totalPages", orders.getTotalPages());
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting user orders: {}", e.getMessage());
            return errorResponse("Failed to get user orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get order details by ID
     * @param id Order ID
     * @return Order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get order
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return errorResponse("Order not found", HttpStatus.NOT_FOUND);
            }

            OrderDTO order = orderOpt.get();

            // Check if user can view this order
            if (!order.getUserId().equals(userId) && !hasRole("ADMIN")) {
                return errorResponse("Unauthorized access to order", HttpStatus.FORBIDDEN);
            }

            return successResponse(order);
        } catch (Exception e) {
            log.error("Error getting order details: {}", e.getMessage());
            return errorResponse("Failed to get order details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculate shipping cost based on cart total
     * @param totalAmount Cart total amount
     * @return Shipping cost
     */
    @GetMapping("/shipping")
    public ResponseEntity<Map<String, Object>> calculateShippingCost(@RequestParam BigDecimal totalAmount) {
        try {
            BigDecimal shippingAmount = calculateShipping(totalAmount);
            
            Map<String, Object> data = new HashMap<>();
            data.put("shippingAmount", shippingAmount);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error calculating shipping cost: {}", e.getMessage());
            return errorResponse("Failed to calculate shipping cost: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to get user ID from email
     * @param email The user's email
     * @return The user's ID or null if the user is not found
     */
    private Long getUserIdFromEmail(String email) {
        Optional<UserDTO> userOpt = userService.findByEmail(email);
        return userOpt.map(UserDTO::getId).orElse(null);
    }

    /**
     * Helper method to enrich a cart item with product details
     * @param cartItem The cart item
     * @param user The user
     * @return The cart item with product details
     */
    private CartItemWithProductDTO enrichCartItemWithProduct(CartDTO cartItem, UserDTO user) {
        Optional<ProductDTO> productOpt = productService.findById(cartItem.getProductId());
        if (productOpt.isEmpty()) {
            return null;
        }

        ProductDTO product = productOpt.get();
        BigDecimal effectivePrice = pricingService.getEffectivePrice(product, user);

        return CartItemWithProductDTO.builder()
                .cartItem(cartItem)
                .product(product)
                .effectivePrice(effectivePrice)
                .itemTotal(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }

    /**
     * Helper method to calculate cart total
     * @param items Cart items with product details
     * @return Cart total amount
     */
    private BigDecimal calculateCartTotal(List<CartItemWithProductDTO> items) {
        return items.stream()
                .map(CartItemWithProductDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Helper method to calculate shipping cost
     * @param totalAmount Cart total amount
     * @return Shipping cost
     */
    private BigDecimal calculateShipping(BigDecimal totalAmount) {
        // Free shipping for orders over 50,000 KZT
        if (totalAmount.compareTo(BigDecimal.valueOf(50000)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(2000); // 2000 KZT shipping
    }
}