package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.service.MessageService;
import kz.arannati.arannati.service.OrderService;
import kz.arannati.arannati.service.ProductService;
import kz.arannati.arannati.service.UserService;
import kz.arannati.arannati.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling admin functionality
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    // Standard rejection message template
    private static final String DEFAULT_REJECTION_MESSAGE = "We regret to inform you that your application to become a cosmetologist has been declined. ";

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final WishlistService wishlistService;
    private final MessageService messageService;

    /**
     * Admin users management page
     */
    @GetMapping("/users")
    public String users(Model model) {
        // Get the authenticated admin user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserDTO> adminOpt = userService.findByEmail(email);

        if (adminOpt.isEmpty()) {
            return "redirect:/admin";
        }

        // Add current user ID to model so admin can't ban themselves
        model.addAttribute("currentUserId", adminOpt.get().getId());

        // Get all users
        List<UserDTO> users = userService.findAll();
        model.addAttribute("users", users);

        return "admin/users";
    }

    /**
     * Toggle user active status (ban/unban)
     */
    @PostMapping("/users/{id}/toggle-active")
    @ResponseBody
    public ResponseEntity<String> toggleUserActive(@PathVariable Long id) {
        boolean success = userService.toggleActive(id);
        if (success) {
            return ResponseEntity.ok("User status updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to update user status");
        }
    }

    /**
     * Approve cosmetologist
     */
    @PostMapping("/cosmetologists/{id}/approve")
    @ResponseBody
    public ResponseEntity<String> approveCosmetologist(@PathVariable Long id) {
        boolean success = userService.approveCosmetologist(id);
        if (success) {
            return ResponseEntity.ok("Cosmetologist approved successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to approve cosmetologist");
        }
    }

    /**
     * Decline cosmetologist
     */
    @PostMapping("/cosmetologists/{id}/decline")
    @ResponseBody
    public ResponseEntity<String> declineCosmetologist(@PathVariable Long id, @RequestParam(required = false) String reason) {
        // Use default message if reason is not provided
        String rejectionReason = DEFAULT_REJECTION_MESSAGE;

        // Append custom reason if provided
        if (reason != null && !reason.trim().isEmpty()) {
            rejectionReason += reason;
        }

        boolean success = userService.declineCosmetologist(id, rejectionReason);
        if (success) {
            return ResponseEntity.ok("Cosmetologist declined successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to decline cosmetologist");
        }
    }

    /**
     * Admin cosmetologist verification page
     */
    @GetMapping("/cosmetologists/verify")
    public String verifyCosmetologists(Model model) {
        List<UserDTO> pendingCosmetologists = userService.findPendingCosmetologists();
        model.addAttribute("pendingCosmetologists", pendingCosmetologists);
        return "admin/verify-cosmetologists";
    }

    /**
     * Admin products management page
     */
    @GetMapping("/products")
    public String products(Model model) {
        List<ProductDTO> products = productService.findAll();
        model.addAttribute("products", products);
        return "admin/products";
    }

    /**
     * Admin edit product page
     */
    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        Optional<ProductDTO> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", productOpt.get());
        return "admin/edit-product";
    }

    /**
     * Update product
     */
    @PostMapping("/products/{id}/update")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO) {
        // Ensure the ID is set correctly
        productDTO.setId(id);

        // Get existing product to preserve fields that aren't updated
        Optional<ProductDTO> existingProductOpt = productService.findById(id);
        if (existingProductOpt.isPresent()) {
            ProductDTO existingProduct = existingProductOpt.get();

            // Only update prices and description if they're provided
            if (productDTO.getRegularPrice() != null) {
                existingProduct.setRegularPrice(productDTO.getRegularPrice());
            }

            if (productDTO.getCosmetologistPrice() != null) {
                existingProduct.setCosmetologistPrice(productDTO.getCosmetologistPrice());
            }

            if (productDTO.getSalePrice() != null) {
                existingProduct.setSalePrice(productDTO.getSalePrice());
            }

            if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
                existingProduct.setDescription(productDTO.getDescription());
            }

            // Save the updated product
            productService.save(existingProduct);
        }

        return "redirect:/admin/products";
    }

    /**
     * Admin orders management page
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        List<OrderDTO> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    /**
     * Admin order details page
     */
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Optional<OrderDTO> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", orderOpt.get());
        return "admin/order-details";
    }

    /**
     * Admin wishlists management page
     */
    @GetMapping("/wishlists")
    public String wishlists(Model model) {
        List<WishlistDTO> wishlists = wishlistService.findAll();
        model.addAttribute("wishlists", wishlists);
        return "admin/wishlists";
    }

    /**
     * Admin wishlist details page
     */
    @GetMapping("/wishlists/{id}")
    public String wishlistDetails(@PathVariable Long id, Model model) {
        Optional<WishlistDTO> wishlistOpt = wishlistService.findById(id);
        if (wishlistOpt.isEmpty()) {
            return "redirect:/admin/wishlists";
        }
        model.addAttribute("wishlist", wishlistOpt.get());
        return "admin/wishlist-details";
    }

    /**
     * Send message to user
     */
    @PostMapping("/messages/send")
    @ResponseBody
    public ResponseEntity<String> sendMessage(@RequestParam Long userId, @RequestParam String message) {
        // Get the authenticated admin user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserDTO> adminOpt = userService.findByEmail(email);

        if (adminOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin user not found");
        }

        // Send the message
        messageService.sendMessage(adminOpt.get().getId(), userId, message);
        return ResponseEntity.ok("Message sent successfully");
    }

    /**
     * Admin messages page
     */
    @GetMapping("/messages")
    public String messages(Model model) {
        // Get the authenticated admin user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserDTO> adminOpt = userService.findByEmail(email);

        if (adminOpt.isEmpty()) {
            return "redirect:/admin";
        }

        // Get sent messages
        List<MessageDTO> sentMessages = messageService.findBySenderId(adminOpt.get().getId());
        model.addAttribute("sentMessages", sentMessages);

        // Get received messages
        List<MessageDTO> receivedMessages = messageService.findByRecipientId(adminOpt.get().getId());
        model.addAttribute("receivedMessages", receivedMessages);

        return "admin/messages";
    }

    /**
     * Get list of users for message recipients
     */
    @GetMapping("/users/list")
    @ResponseBody
    public List<UserDTO> getUsersList() {
        return userService.findAll();
    }

    /**
     * Mark message as read
     */
    @PostMapping("/messages/{id}/read")
    @ResponseBody
    public ResponseEntity<String> markMessageAsRead(@PathVariable Long id) {
        boolean success = messageService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok("Message marked as read");
        } else {
            return ResponseEntity.badRequest().body("Failed to mark message as read");
        }
    }

    /**
     * Delete message
     */
    @PostMapping("/messages/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteById(id);
            return ResponseEntity.ok("Message deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete message");
        }
    }
}
