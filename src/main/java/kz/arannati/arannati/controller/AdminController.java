package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.CosmetologistVerificationDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.WishlistDTO;
import kz.arannati.arannati.entity.Material;
import kz.arannati.arannati.service.CosmetologistVerificationService;
import kz.arannati.arannati.service.FileStorageService;
import kz.arannati.arannati.service.MaterialService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

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
    private final MaterialService materialService;
    private final CosmetologistVerificationService cosmetologistVerificationService;
    private final FileStorageService fileStorageService;

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
        model.addAttribute("cosmetologists", pendingCosmetologists);

        // Add recently verified cosmetologists (using all verified cosmetologists for now)
        List<UserDTO> recentlyVerified = userService.findVerifiedCosmetologists();
        model.addAttribute("recentlyVerified", recentlyVerified);

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
     * Admin pending orders page
     */
    @GetMapping("/orders/pending")
    public String pendingOrders(Model model) {
        // Get orders with PENDING status
        Page<OrderDTO> pendingOrdersPage = orderService.findByStatusOrderByCreatedAtDesc("PENDING", Pageable.unpaged());
        List<OrderDTO> pendingOrders = pendingOrdersPage.getContent();
        model.addAttribute("orders", pendingOrders);
        // Set the active filter to pending
        model.addAttribute("activeFilter", "pending");
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

    /**
     * Update order status
     * Note: This is a placeholder implementation. In a real application, you would
     * implement the updateStatus method in the OrderService.
     */
    @PostMapping("/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            // Get the order
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Order not found");
            }

            // Update the status
            OrderDTO order = orderOpt.get();
            order.setStatus(status);

            // Save the updated order
            orderService.save(order);

            return ResponseEntity.ok("Order status updated successfully");
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update order status");
        }
    }

    /**
     * Add note to order
     * Note: This is a placeholder implementation. In a real application, you would
     * implement the addNote method in the OrderService.
     */
    @PostMapping("/orders/{id}/note")
    @ResponseBody
    public ResponseEntity<String> addOrderNote(@PathVariable Long id, @RequestParam String note) {
        try {
            // Get the order
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Order not found");
            }

            // Update the notes
            OrderDTO order = orderOpt.get();
            String currentNotes = order.getNotes() != null ? order.getNotes() : "";
            order.setNotes(currentNotes + "\n" + note);

            // Save the updated order
            orderService.save(order);

            return ResponseEntity.ok("Note added successfully");
        } catch (Exception e) {
            log.error("Error adding note to order: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to add note");
        }
    }

    /**
     * Admin add product page
     * Note: This is a placeholder implementation. In a real application, you would
     * implement the getAllCategories method in the ProductService.
     */
    @GetMapping("/products/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new ProductDTO());

        // Placeholder for categories
        List<String> categories = new ArrayList<>();
        categories.add("Уход за лицом");
        categories.add("Уход за телом");
        categories.add("Уход за волосами");
        categories.add("Макияж");
        categories.add("Парфюмерия");

        model.addAttribute("categories", categories);
        return "admin/edit-product";
    }

    /**
     * Add new product
     */
    @PostMapping("/products/add")
    public String addNewProduct(@ModelAttribute ProductDTO productDTO) {
        try {
            // Set default values for required fields that are not in the form
            if (productDTO.getSku() == null || productDTO.getSku().isEmpty()) {
                productDTO.setSku("SKU-" + System.currentTimeMillis());
            }

            if (productDTO.getCategoryId() == null) {
                productDTO.setCategoryId(1L); // Default category ID
            }

            if (productDTO.getBrandId() == null) {
                productDTO.setBrandId(1L); // Default brand ID
            }

            if (productDTO.getRegularPrice() == null) {
                productDTO.setRegularPrice(new BigDecimal("0.01"));
            }

            if (productDTO.getStockQuantity() == null) {
                productDTO.setStockQuantity(0);
            }

            if (productDTO.getSortOrder() == null) {
                productDTO.setSortOrder(0);
            }

            productService.save(productDTO);
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("Error adding new product: {}", e.getMessage(), e);
            return "redirect:/admin/products/add?error=true";
        }
    }

    /**
     * Delete product
     * Note: This is a placeholder implementation. In a real application, you would
     * modify the deleteById method in the ProductService to return a boolean.
     */
    @PostMapping("/products/{id}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            // Check if product exists
            Optional<ProductDTO> productOpt = productService.findById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Product not found");
            }

            // Delete the product
            productService.deleteById(id);

            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete product");
        }
    }

    /**
     * Cancel order
     * Note: This is a placeholder implementation. In a real application, you would
     * implement the cancelOrder method in the OrderService.
     */
    @PostMapping("/orders/{id}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        try {
            // Get the order
            Optional<OrderDTO> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Order not found");
            }

            // Update the status to CANCELLED
            OrderDTO order = orderOpt.get();
            order.setStatus("CANCELLED");

            // Save the updated order
            orderService.save(order);

            return ResponseEntity.ok("Order cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to cancel order");
        }
    }

    /**
     * Admin materials upload page
     */
    @GetMapping("/materials/upload")
    public String materialsUpload(Model model) {
        // Get the authenticated admin user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserDTO> adminOpt = userService.findByEmail(email);

        if (adminOpt.isEmpty()) {
            return "redirect:/admin";
        }

        // Get recently uploaded materials
        List<Material> recentMaterials = materialService.findAllByOrderByUploadDateDesc();
        model.addAttribute("materials", recentMaterials);

        return "admin/materials-upload";
    }

    /**
     * Upload materials
     */
    @PostMapping("/materials/upload")
    public String uploadMaterials(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description) {
        try {
            // Get the authenticated admin user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Optional<UserDTO> adminOpt = userService.findByEmail(email);

            if (adminOpt.isEmpty()) {
                return "redirect:/admin/materials/upload?error=true";
            }

            // Save the material
            materialService.saveMaterial(file, title, description, email);

            return "redirect:/admin/materials/upload?success=true";
        } catch (Exception e) {
            log.error("Error uploading materials: {}", e.getMessage(), e);
            return "redirect:/admin/materials/upload?error=true";
        }
    }

    /**
     * Delete a material
     * @param id The ID of the material to delete
     * @return Redirect to the materials upload page
     */
    @GetMapping("/materials/{id}/delete")
    public String deleteMaterial(@PathVariable Long id) {
        boolean success = materialService.deleteById(id);
        if (success) {
            return "redirect:/admin/materials/upload?success=true";
        } else {
            return "redirect:/admin/materials/upload?error=true";
        }
    }

    /**
     * Get cosmetologist documents
     */
    @GetMapping("/cosmetologists/{id}/documents")
    @ResponseBody
    public ResponseEntity<?> getCosmetologistDocuments(@PathVariable Long id) {
        try {
            Optional<UserDTO> cosmetologistOpt = userService.findById(id);
            if (cosmetologistOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Cosmetologist not found");
            }

            UserDTO cosmetologist = cosmetologistOpt.get();

            // Get verification record for the cosmetologist
            Optional<CosmetologistVerificationDTO> verificationOpt = cosmetologistVerificationService.findByUserId(id);
            if (verificationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Verification record not found");
            }

            CosmetologistVerificationDTO verification = verificationOpt.get();

            // Create a map for the response
            Map<String, Object> response = new HashMap<>();
            response.put("cosmetologistName", cosmetologist.getFirstName() + " " + cosmetologist.getLastName());

            // Create a list of documents
            List<Map<String, Object>> documents = new ArrayList<>();

            // Add diploma document
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", verification.getId());
            doc.put("title", "Диплом об образовании");
            doc.put("type", verification.getDiplomaOriginalFilename().toLowerCase().endsWith(".pdf") ? "pdf" : "image");
            doc.put("url", "/admin/cosmetologists/" + id + "/diploma");
            doc.put("filename", verification.getDiplomaOriginalFilename());
            doc.put("description", "Диплом об образовании из " + verification.getInstitutionName());
            documents.add(doc);

            // Add documents to response
            response.put("documents", documents);
            response.put("verification", verification);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cosmetologist documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to get cosmetologist documents");
        }
    }

    /**
     * Download cosmetologist diploma
     */
    @GetMapping("/cosmetologists/{id}/diploma")
    public ResponseEntity<?> downloadCosmetologistDiploma(@PathVariable Long id) {
        try {
            // Get verification record for the cosmetologist
            Optional<CosmetologistVerificationDTO> verificationOpt = cosmetologistVerificationService.findByUserId(id);
            if (verificationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Verification record not found");
            }

            CosmetologistVerificationDTO verification = verificationOpt.get();

            // Get the file path
            Path filePath = fileStorageService.getFilePath(verification.getDiplomaFilePath(), "cosmetologist-diplomas");

            // Check if file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType;
            String filename = verification.getDiplomaOriginalFilename().toLowerCase();
            if (filename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filename.endsWith(".png")) {
                contentType = "image/png";
            } else {
                contentType = "application/octet-stream";
            }

            // Return the file
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + verification.getDiplomaOriginalFilename() + "\"")
                .body(new org.springframework.core.io.InputStreamResource(Files.newInputStream(filePath)));
        } catch (Exception e) {
            log.error("Error downloading cosmetologist diploma: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to download cosmetologist diploma");
        }
    }
}
