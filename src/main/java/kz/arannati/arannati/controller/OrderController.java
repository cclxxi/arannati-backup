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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final PricingService pricingService;
    private final ProductService productService;
    private final NotificationService notificationService;

    /**
     * Checkout page
     */
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(user.getId());
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        List<CartItemWithProductDTO> cartItemsWithProducts = cartItems.stream()
                .map(this::enrichCartItemWithProduct)
                .toList();

        BigDecimal totalAmount = calculateCartTotal(cartItemsWithProducts, user);
        BigDecimal shippingAmount = calculateShipping(totalAmount);
        BigDecimal finalAmount = totalAmount.add(shippingAmount);

        model.addAttribute("cartItems", cartItemsWithProducts);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingAmount", shippingAmount);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("currentUser", user);

        return "orders/checkout";
    }

    /**
     * Create order
     */
    @PostMapping("/api/orders/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            // Validate cart is not empty
            List<CartDTO> cartItems = cartService.findActiveCartItemsByUserId(user.getId());
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Корзина пуста"));
            }

            // Create order
            OrderDTO order = orderService.createOrder(orderCreateDTO, user.getId());

            // Clear cart after successful order
            cartService.deleteAllByUserId(user.getId());

            // Send notification to admins
            notificationService.notifyNewOrder(order.getId());

            log.info("Order created successfully: {}", order.getOrderNumber());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNumber(),
                    "message", "Заказ успешно оформлен"
            ));

        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка оформления заказа"));
        }
    }

    /**
     * Order success page
     */
    @GetMapping("/orders/{id}/success")
    public String orderSuccess(@PathVariable Long id, Model model) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(user.getId())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", orderOpt.get());
        model.addAttribute("currentUser", user);

        return "orders/success";
    }

    /**
     * User orders page
     */
    @GetMapping("/orders")
    public String userOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        model.addAttribute("orders", orders);
        model.addAttribute("currentUser", user);

        return "orders/user-orders";
    }

    /**
     * Order details page
     */
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        UserDTO user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/orders";
        }

        OrderDTO order = orderOpt.get();

        // Check if user can view this order
        if (!order.getUserId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("currentUser", user);

        return "orders/details";
    }

    private UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userService.findByEmail(auth.getName()).orElse(null);
    }

    private CartItemWithProductDTO enrichCartItemWithProduct(CartDTO cartItem) {
        Optional<ProductDTO> productOpt = productService.findById(cartItem.getProductId());
        if (productOpt.isEmpty()) {
            return null;
        }

        ProductDTO product = productOpt.get();
        UserDTO user = getCurrentUser();
        BigDecimal effectivePrice = pricingService.getEffectivePrice(product, user);

        return CartItemWithProductDTO.builder()
                .cartItem(cartItem)
                .product(product)
                .effectivePrice(effectivePrice)
                .itemTotal(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }

    private BigDecimal calculateCartTotal(List<CartItemWithProductDTO> items, UserDTO user) {
        return items.stream()
                .map(CartItemWithProductDTO::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateShipping(BigDecimal totalAmount) {
        // Free shipping for orders over 50,000 KZT
        if (totalAmount.compareTo(BigDecimal.valueOf(50000)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(2000); // 2000 KZT shipping
    }
}