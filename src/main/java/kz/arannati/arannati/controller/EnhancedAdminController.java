package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class EnhancedAdminController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final OrderService orderService;
    private final CartService cartService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    /**
     * Enhanced dashboard with comprehensive statistics
     */
    @GetMapping("/dashboard")
    public String enhancedDashboard(Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        // User statistics
        long totalUsers = userService.countByRoleAndActiveIsTrue("USER");
        long totalCosmetologists = userService.countByRoleAndActiveIsTrue("COSMETOLOGIST");
        long pendingCosmetologists = userService.findPendingCosmetologists().size();

        // Product statistics
        long totalProducts = productService.countByActiveIsTrue();
        long lowStockProducts = productService.countLowStockProducts(10);

        // Order statistics
        List<OrderDTO> recentOrders = orderService.findRecentOrders(10);
        List<OrderDTO> pendingOrders = orderService.findByStatus("PENDING");

        // Notifications
        List<NotificationDTO> adminNotifications = notificationService.findByUserIdOrderByCreatedAtDesc(admin.getId());

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCosmetologists", totalCosmetologists);
        model.addAttribute("pendingCosmetologists", pendingCosmetologists);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("notifications", adminNotifications.stream().limit(5).toList());
        model.addAttribute("currentUser", admin);

        return "admin/enhanced-dashboard";
    }

    /**
     * Product management page
     */
    @GetMapping("/products")
    public String productManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            Model model) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search, pageable);
        } else if (categoryId != null || brandId != null) {
            products = productService.findProductsWithFilters(categoryId, brandId, null, null, null, pageable);
        } else {
            products = productService.findAllWithPagination(pageable);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("currentUser", admin);

        return "admin/products";
    }

    /**
     * Add/Edit product page
     */
    @GetMapping("/products/add")
    public String addProductPage(Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("product", new ProductDTO());
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("currentUser", admin);
        model.addAttribute("isEdit", false);

        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductPage(@PathVariable Long id, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("brands", brandService.findAllActive());
        model.addAttribute("currentUser", admin);
        model.addAttribute("isEdit", true);

        return "admin/product-form";
    }

    /**
     * Save product
     */
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute ProductDTO productDTO,
                              @RequestParam(required = false) MultipartFile[] images) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
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

            // Handle image uploads if provided
            if (images != null && images.length > 0) {
                // TODO: Implement image upload service
                log.info("Image upload for product {} - {} images", savedProduct.getId(), images.length);
            }

            log.info("Product saved: {}", savedProduct.getName());
            return "redirect:/admin/products";

        } catch (Exception e) {
            log.error("Error saving product", e);
            return "redirect:/admin/products/add?error=save";
        }
    }

    /**
     * Update product pricing
     */
    @PostMapping("/api/products/{id}/pricing")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProductPricing(
            @PathVariable Long id,
            @RequestParam BigDecimal regularPrice,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(required = false) BigDecimal discountPercentage) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Товар не найден"));
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

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Цены обновлены",
                    "regularPrice", product.getRegularPrice(),
                    "salePrice", product.getSalePrice(),
                    "cosmetologistPrice", product.getCosmetologistPrice(),
                    "adminPrice", product.getAdminPrice()
            ));

        } catch (Exception e) {
            log.error("Error updating product pricing", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления цен"));
        }
    }

    /**
     * Update product stock
     */
    @PostMapping("/api/products/{id}/stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer stockQuantity) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Товар не найден"));
            }

            ProductDTO product = productOpt.get();
            product.setStockQuantity(stockQuantity);
            productService.save(product);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Количество обновлено",
                    "stockQuantity", stockQuantity
            ));

        } catch (Exception e) {
            log.error("Error updating product stock", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления количества"));
        }
    }

    /**
     * Orders management page
     */
    @GetMapping("/orders")
    public String orderManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            Model model) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders;

        if (status != null && !status.isEmpty()) {
            orders = orderService.findByStatusWithPagination(status, pageable);
        } else {
            orders = orderService.findAllOrderByCreatedAtDesc(pageable);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentUser", admin);

        return "admin/orders";
    }

    /**
     * Order details for admin
     */
    @GetMapping("/orders/{id}")
    public String adminOrderDetails(@PathVariable Long id, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/admin/orders";
        }

        OrderDTO order = orderOpt.get();
        Optional<UserDTO> customer = userService.findById(order.getUserId());

        model.addAttribute("order", order);
        model.addAttribute("customer", customer.orElse(null));
        model.addAttribute("currentUser", admin);

        return "admin/order-details";
    }

    /**
     * Update order status
     */
    @PostMapping("/api/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);

            // Notify customer about status change
            notificationService.createNotification(
                    updatedOrder.getUserId(),
                    "Статус заказа изменен",
                    "Статус заказа №" + updatedOrder.getOrderNumber() + " изменен на: " + status,
                    kz.arannati.arannati.enums.NotificationType.ORDER_STATUS_CHANGED,
                    id,
                    "Order"
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Статус заказа обновлен",
                    "newStatus", status
            ));

        } catch (Exception e) {
            log.error("Error updating order status", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка обновления статуса заказа"));
        }
    }

    /**
     * Cancel order
     */
    @PostMapping("/api/orders/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            OrderDTO cancelledOrder = orderService.cancelOrder(id);

            // Notify customer about cancellation
            notificationService.createNotification(
                    cancelledOrder.getUserId(),
                    "Заказ отменен",
                    "Заказ №" + cancelledOrder.getOrderNumber() + " был отменен администратором",
                    kz.arannati.arannati.enums.NotificationType.ORDER_STATUS_CHANGED,
                    id,
                    "Order"
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Заказ отменен"
            ));

        } catch (Exception e) {
            log.error("Error cancelling order", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка отмены заказа"));
        }
    }

    /**
     * View user's cart (admin function)
     */
    @GetMapping("/users/{userId}/cart")
    public String viewUserCart(@PathVariable Long userId, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<UserDTO> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(userId);
        UserDTO user = userOpt.get();

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("currentUser", admin);

        return "admin/user-cart";
    }

    /**
     * View user's wishlist (admin function)
     */
    @GetMapping("/users/{userId}/wishlist")
    public String viewUserWishlist(@PathVariable Long userId, Model model) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Optional<UserDTO> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        List<WishlistDTO> wishlistItems = wishlistService.findActiveWishlistItemsByUserId(userId);
        UserDTO user = userOpt.get();

        model.addAttribute("user", user);
        model.addAttribute("wishlistItems", wishlistItems);
        model.addAttribute("currentUser", admin);

        return "admin/user-wishlist";
    }

    /**
     * Reviews management
     */
    @GetMapping("/reviews")
    public String reviewsManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long productId,
            Model model) {

        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return "redirect:/auth/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews;

        if (productId != null) {
            reviews = reviewService.findByProductIdAndActiveIsTrue(productId, pageable);
        } else {
            reviews = reviewService.findAllWithPagination(pageable);
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("productId", productId);
        model.addAttribute("currentUser", admin);

        return "admin/reviews";
    }

    /**
     * Delete review
     */
    @PostMapping("/api/reviews/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long id) {
        UserDTO admin = getCurrentAdmin();
        if (admin == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Доступ запрещен"));
        }

        try {
            reviewService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Отзыв удален"
            ));

        } catch (Exception e) {
            log.error("Error deleting review", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка удаления отзыва"));
        }
    }

    private UserDTO getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        Optional<UserDTO> userOpt = userService.findByEmail(auth.getName());
        if (userOpt.isEmpty() || !"ADMIN".equals(userOpt.get().getRole())) {
            return null;
        }

        return userOpt.get();
    }
}