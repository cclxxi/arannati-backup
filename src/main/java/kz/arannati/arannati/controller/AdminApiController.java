package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.enums.NotificationType;
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

/**
 * REST API controller for admin functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminApiController extends BaseApiController {

    private final UserService userService;
    private final ChatService chatService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final OrderService orderService;
    private final CartService cartService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    /**
     * Get admin dashboard statistics
     * @return Dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get statistics
            long totalUsers = userService.countByRoleAndActiveIsTrue("USER");
            long totalCosmetologists = userService.countByRoleAndActiveIsTrue("COSMETOLOGIST");
            long pendingCosmetologists = userService.findPendingCosmetologists().size();
            long activeSupportRequests = chatService.getActiveSupportRequests().size();
            long unreadMessages = chatService.getUnreadMessagesCount(userId);

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", totalUsers);
            data.put("totalCosmetologists", totalCosmetologists);
            data.put("pendingCosmetologists", pendingCosmetologists);
            data.put("activeSupportRequests", activeSupportRequests);
            data.put("unreadMessages", unreadMessages);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting admin stats: {}", e.getMessage());
            return errorResponse("Failed to get admin stats: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all users
     * @return List of users
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Get all users except current admin
            List<UserDTO> users = userService.findAll().stream()
                    .filter(user -> !user.getId().equals(userId))
                    .toList();

            return successResponse(users);
        } catch (Exception e) {
            log.error("Error getting users: {}", e.getMessage());
            return errorResponse("Failed to get users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Approve a cosmetologist
     * @param id Cosmetologist ID
     * @return Success message
     */
    @PostMapping("/cosmetologists/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveCosmetologist(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            boolean success = userService.approveCosmetologist(id);
            if (success) {
                log.info("Admin {} approved cosmetologist {}", email, id);
                return successResponse(null, "Cosmetologist approved successfully");
            } else {
                return errorResponse("Failed to approve cosmetologist", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error approving cosmetologist: {}", e.getMessage());
            return errorResponse("Failed to approve cosmetologist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Decline a cosmetologist
     * @param id Cosmetologist ID
     * @param reason Reason for declining
     * @return Success message
     */
    @PostMapping("/cosmetologists/{id}/decline")
    public ResponseEntity<Map<String, Object>> declineCosmetologist(
            @PathVariable Long id,
            @RequestParam String reason) {

        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            boolean declined = userService.declineCosmetologist(id, reason);
            if (!declined) {
                return errorResponse("Failed to decline cosmetologist", HttpStatus.BAD_REQUEST);
            }

            log.info("Admin {} declined cosmetologist {} with reason: {}", email, id, reason);
            return successResponse(null, "Cosmetologist declined successfully");
        } catch (Exception e) {
            log.error("Error declining cosmetologist: {}", e.getMessage());
            return errorResponse("Failed to decline cosmetologist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggle user active status (ban/unban)
     * @param id User ID
     * @return Success message
     */
    @PostMapping("/users/{id}/toggle-active")
    public ResponseEntity<Map<String, Object>> toggleUserActive(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            boolean success = userService.toggleActive(id);
            if (success) {
                log.info("Admin {} toggled active status for user {}", email, id);
                return successResponse(null, "User active status toggled successfully");
            } else {
                return errorResponse("Failed to toggle user active status", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error toggling user active status: {}", e.getMessage());
            return errorResponse("Failed to toggle user active status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a message to a user
     * @param id User ID
     * @param message Message content
     * @return Created message
     */
    @PostMapping("/users/{id}/send-message")
    public ResponseEntity<Map<String, Object>> sendMessageToUser(
            @PathVariable Long id,
            @RequestParam String message) {

        String email = getCurrentUserEmail();

        // If user is not authenticated or not an admin, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        if (!hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            // Get user ID from email
            Long userId = getUserIdFromEmail(email);

            if (userId == null) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            // Send message
            MessageDTO sentMessage = chatService.sendDirectMessage(userId, id, message);
            log.info("Admin {} sent message to user {}", email, id);

            Map<String, Object> data = new HashMap<>();
            data.put("message", sentMessage);
            data.put("chatId", sentMessage.getChatId());

            return successResponse(data, "Message sent successfully");
        } catch (Exception e) {
            log.error("Error sending message to user: {}", e.getMessage());
            return errorResponse("Failed to send message: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get products with pagination and optional filtering
     * @param page Page number (0-based)
     * @param size Page size
     * @param search Optional search term
     * @param categoryId Optional category ID filter
     * @param brandId Optional brand ID filter
     * @return List of products matching the criteria
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDTO> products;

            if (search != null && !search.isEmpty()) {
                products = productService.searchProducts(search, pageable);
            } else if (categoryId != null || brandId != null) {
                products = productService.findProductsWithFilters(categoryId, brandId, null, null, null, pageable);
            } else {
                products = productService.findAllWithPagination(pageable);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("products", products.getContent());
            data.put("currentPage", products.getNumber());
            data.put("totalItems", products.getTotalElements());
            data.put("totalPages", products.getTotalPages());
            data.put("categories", categoryService.findAllActive());
            data.put("brands", brandService.findAllActive());

            if (search != null) data.put("search", search);
            if (categoryId != null) data.put("categoryId", categoryId);
            if (brandId != null) data.put("brandId", brandId);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting products: {}", e.getMessage());
            return errorResponse("Failed to get products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get product by ID
     * @param id Product ID
     * @return Product details
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return errorResponse("Product not found", HttpStatus.NOT_FOUND);
            }

            return successResponse(productOpt.get());
        } catch (Exception e) {
            log.error("Error getting product: {}", e.getMessage());
            return errorResponse("Failed to get product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create or update a product
     * @param productDTO Product data
     * @return Created or updated product
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> saveProduct(@RequestBody ProductDTO productDTO) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            // Set special prices based on regular price if not set
            if (productDTO.getCosmetologistPrice() == null && productDTO.getRegularPrice() != null) {
                productDTO.setCosmetologistPrice(
                        productDTO.getRegularPrice().multiply(BigDecimal.valueOf(0.75))
                );
            }

            if (productDTO.getAdminPrice() == null && productDTO.getRegularPrice() != null) {
                productDTO.setAdminPrice(
                        productDTO.getRegularPrice().multiply(BigDecimal.valueOf(0.60))
                );
            }

            ProductDTO savedProduct = productService.save(productDTO);
            log.info("Product saved: {}", savedProduct.getName());

            return successResponse(savedProduct, "Product saved successfully");
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage());
            return errorResponse("Failed to save product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update product pricing
     * @param id Product ID
     * @param regularPrice Regular price
     * @param salePrice Optional sale price
     * @param discountPercentage Optional discount percentage
     * @return Updated product pricing
     */
    @PutMapping("/products/{id}/pricing")
    public ResponseEntity<Map<String, Object>> updateProductPricing(
            @PathVariable Long id,
            @RequestParam BigDecimal regularPrice,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(required = false) BigDecimal discountPercentage) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return errorResponse("Product not found", HttpStatus.NOT_FOUND);
            }

            ProductDTO product = productOpt.get();
            BigDecimal oldPrice = product.getRegularPrice();

            product.setRegularPrice(regularPrice);

            // Calculate sale price from discount percentage if provided
            if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = regularPrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
                product.setSalePrice(regularPrice.subtract(discount));
            } else {
                product.setSalePrice(salePrice);
            }

            // Update special prices proportionally
            product.setCosmetologistPrice(regularPrice.multiply(BigDecimal.valueOf(0.75)));
            product.setAdminPrice(regularPrice.multiply(BigDecimal.valueOf(0.60)));

            productService.save(product);

            // Check if price was reduced and notify wishlist users
            if (oldPrice != null && regularPrice.compareTo(oldPrice) < 0) {
                List<Long> wishlistUserIds = wishlistService.findActiveWishlistItemsByUserId(id)
                        .stream()
                        .map(WishlistDTO::getUserId)
                        .distinct()
                        .toList();

                if (!wishlistUserIds.isEmpty()) {
                    notificationService.notifyPriceReduction(id, wishlistUserIds);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("regularPrice", product.getRegularPrice());
            data.put("salePrice", product.getSalePrice());
            data.put("cosmetologistPrice", product.getCosmetologistPrice());
            data.put("adminPrice", product.getAdminPrice());

            return successResponse(data, "Prices updated successfully");
        } catch (Exception e) {
            log.error("Error updating product pricing: {}", e.getMessage());
            return errorResponse("Failed to update product pricing: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update product stock
     * @param id Product ID
     * @param stockQuantity Stock quantity
     * @return Updated stock quantity
     */
    @PutMapping("/products/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer stockQuantity) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return errorResponse("Product not found", HttpStatus.NOT_FOUND);
            }

            ProductDTO product = productOpt.get();
            product.setStockQuantity(stockQuantity);
            productService.save(product);

            Map<String, Object> data = new HashMap<>();
            data.put("stockQuantity", stockQuantity);

            return successResponse(data, "Stock quantity updated successfully");
        } catch (Exception e) {
            log.error("Error updating product stock: {}", e.getMessage());
            return errorResponse("Failed to update product stock: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get orders with pagination and optional filtering
     * @param page Page number (0-based)
     * @param size Page size
     * @param status Optional status filter
     * @return List of orders matching the criteria
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDTO> orders;

            if (status != null && !status.isEmpty()) {
                orders = orderService.findByStatusWithPagination(status, pageable);
            } else {
                orders = orderService.findAllOrderByCreatedAtDesc(pageable);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("orders", orders.getContent());
            data.put("currentPage", orders.getNumber());
            data.put("totalItems", orders.getTotalElements());
            data.put("totalPages", orders.getTotalPages());

            if (status != null) data.put("status", status);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting orders: {}", e.getMessage());
            return errorResponse("Failed to get orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get order details by ID
     * @param id Order ID
     * @return Order details including customer information
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return errorResponse("Order not found", HttpStatus.NOT_FOUND);
            }

            OrderDTO order = orderOpt.get();

            // Get customer information
            Optional<UserDTO> customerOpt = userService.findById(order.getUserId());

            Map<String, Object> data = new HashMap<>();
            data.put("order", order);
            customerOpt.ifPresent(customer -> data.put("customer", customer));

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting order details: {}", e.getMessage());
            return errorResponse("Failed to get order details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update order status
     * @param id Order ID
     * @param status New status
     * @return Updated order
     */
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);

            // Notify customer about status change
            notificationService.createNotification(
                    updatedOrder.getUserId(),
                    "Order status updated",
                    "Your order #" + updatedOrder.getOrderNumber() + " status has been updated to: " + status,
                    NotificationType.ORDER_STATUS_CHANGED,
                    id,
                    "Order"
            );

            return successResponse(updatedOrder, "Order status updated successfully");
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return errorResponse("Failed to update order status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Cancel an order
     * @param id Order ID
     * @return Success message
     */
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            OrderDTO cancelledOrder = orderService.cancelOrder(id);

            // Notify customer about cancellation
            notificationService.createNotification(
                    cancelledOrder.getUserId(),
                    "Order cancelled",
                    "Your order #" + cancelledOrder.getOrderNumber() + " has been cancelled by an administrator",
                    NotificationType.ORDER_STATUS_CHANGED,
                    id,
                    "Order"
            );

            return successResponse(null, "Order cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage());
            return errorResponse("Failed to cancel order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a user's cart
     * @param userId User ID
     * @return User's cart items
     */
    @GetMapping("/users/{userId}/cart")
    public ResponseEntity<Map<String, Object>> getUserCart(@PathVariable Long userId) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<UserDTO> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("user", userOpt.get());
            data.put("cartItems", cartItems);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting user cart: {}", e.getMessage());
            return errorResponse("Failed to get user cart: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a user's wishlist
     * @param userId User ID
     * @return User's wishlist items
     */
    @GetMapping("/users/{userId}/wishlist")
    public ResponseEntity<Map<String, Object>> getUserWishlist(@PathVariable Long userId) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Optional<UserDTO> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return errorResponse("User not found", HttpStatus.NOT_FOUND);
            }

            List<WishlistDTO> wishlistItems = wishlistService.findActiveWishlistItemsByUserId(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("user", userOpt.get());
            data.put("wishlistItems", wishlistItems);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting user wishlist: {}", e.getMessage());
            return errorResponse("Failed to get user wishlist: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get reviews with pagination and optional filtering
     * @param page Page number (0-based)
     * @param size Page size
     * @param productId Optional product ID filter
     * @return List of reviews matching the criteria
     */
    @GetMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long productId) {

        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewDTO> reviews;

            if (productId != null) {
                reviews = reviewService.findByProductIdAndActiveIsTrue(productId, pageable);
            } else {
                reviews = reviewService.findAllWithPagination(pageable);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("reviews", reviews.getContent());
            data.put("currentPage", reviews.getNumber());
            data.put("totalItems", reviews.getTotalElements());
            data.put("totalPages", reviews.getTotalPages());

            if (productId != null) data.put("productId", productId);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting reviews: {}", e.getMessage());
            return errorResponse("Failed to get reviews: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a review
     * @param id Review ID
     * @return Success message
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long id) {
        String email = getCurrentUserEmail();

        if (email == null || !hasRole("ADMIN")) {
            return errorResponse("Admin role required", HttpStatus.FORBIDDEN);
        }

        try {
            reviewService.deleteById(id);
            return successResponse(null, "Review deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting review: {}", e.getMessage());
            return errorResponse("Failed to delete review: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
}
