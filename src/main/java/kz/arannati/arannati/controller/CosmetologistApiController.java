package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API controller for cosmetologist functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/cosmetologist")
@RequiredArgsConstructor
public class CosmetologistApiController extends BaseApiController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final CatalogExportService catalogExportService;
    private final PricingService pricingService;

    /**
     * Get dashboard data for cosmetologist
     * @return Dashboard data including recent orders and catalog statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            // Recent orders
            Page<OrderDTO> recentOrders = orderService.findByUserIdOrderByCreatedAtDesc(
                    cosmetologist.getId(),
                    PageRequest.of(0, 5)
            );

            // Catalog statistics
            long totalProducts = productService.countByActiveIsTrue();
            long saleProducts = productService.countProductsOnSale();

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("cosmetologist", cosmetologist);
            data.put("recentOrders", recentOrders.getContent());
            data.put("totalProducts", totalProducts);
            data.put("saleProducts", saleProducts);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting cosmetologist dashboard: {}", e.getMessage());
            return errorResponse("Failed to get dashboard data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get professional catalog with special prices for cosmetologists
     * @param categoryId Optional category ID filter
     * @param brandId Optional brand ID filter
     * @param search Optional search term
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sorting parameters (field,direction)
     * @return List of products with cosmetologist pricing
     */
    @GetMapping("/catalog")
    public ResponseEntity<Map<String, Object>> getProfessionalCatalog(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDTO> products;

            if (search != null && !search.isEmpty()) {
                products = productService.searchProducts(search, pageable);
            } else if (categoryId != null || brandId != null) {
                products = productService.findProductsWithFilters(categoryId, brandId, null, null, null, pageable);
            } else {
                products = productService.findByActiveIsTrueOrderBySortOrderAscNameAsc(pageable);
            }

            // Enrich products with cosmetologist pricing
            List<ProductDTO> enrichedProducts = products.getContent().stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        product.setHasDiscount(pricingService.hasSpecialPrice(product, cosmetologist));
                        return product;
                    })
                    .collect(Collectors.toList());

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("products", enrichedProducts);
            data.put("currentPage", products.getNumber());
            data.put("totalItems", products.getTotalElements());
            data.put("totalPages", products.getTotalPages());
            data.put("categories", categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc());
            data.put("brands", brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc());
            
            // Add filter parameters to response
            if (categoryId != null) data.put("categoryId", categoryId);
            if (brandId != null) data.put("brandId", brandId);
            if (search != null) data.put("search", search);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting professional catalog: {}", e.getMessage());
            return errorResponse("Failed to get catalog: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get catalog download options
     * @return List of categories and brands for catalog download
     */
    @GetMapping("/catalogs")
    public ResponseEntity<Map<String, Object>> getCatalogOptions() {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            List<CategoryDTO> categories = categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc();
            List<BrandDTO> brands = brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc();

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("categories", categories);
            data.put("brands", brands);
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting catalog options: {}", e.getMessage());
            return errorResponse("Failed to get catalog options: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download full catalog (PDF or Excel)
     * @param format Format to download (pdf or excel)
     * @return Catalog file
     */
    @GetMapping("/catalogs/download/full")
    public ResponseEntity<?> downloadFullCatalog(@RequestParam String format) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            List<ProductDTO> allProducts = productService.findAllActiveProducts();

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = allProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .collect(Collectors.toList());

            ByteArrayOutputStream outputStream;
            String filename;
            String contentType;

            if ("pdf".equals(format)) {
                outputStream = catalogExportService.exportToPdf(enrichedProducts, "Полный каталог", cosmetologist);
                filename = "full_catalog.pdf";
                contentType = "application/pdf";
            } else if ("excel".equals(format)) {
                outputStream = catalogExportService.exportToExcel(enrichedProducts, "Полный каталог");
                filename = "full_catalog.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                return errorResponse("Invalid format. Use 'pdf' or 'excel'", HttpStatus.BAD_REQUEST);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating catalog: {}", e.getMessage());
            return errorResponse("Failed to generate catalog: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download catalog by brand (PDF or Excel)
     * @param brandId Brand ID
     * @param format Format to download (pdf or excel)
     * @return Catalog file
     */
    @GetMapping("/catalogs/download/brand/{brandId}")
    public ResponseEntity<?> downloadBrandCatalog(
            @PathVariable Long brandId,
            @RequestParam String format) {

        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            Optional<BrandDTO> brandOpt = brandService.findById(brandId);
            if (brandOpt.isEmpty()) {
                return errorResponse("Brand not found", HttpStatus.NOT_FOUND);
            }

            BrandDTO brand = brandOpt.get();
            List<ProductDTO> brandProducts = productService.findByBrandIdAndActiveIsTrue(brandId);

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = brandProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .collect(Collectors.toList());

            ByteArrayOutputStream outputStream;
            String filename;
            String contentType;

            if ("pdf".equals(format)) {
                outputStream = catalogExportService.exportToPdf(enrichedProducts, "Каталог " + brand.getName(), cosmetologist);
                filename = "catalog_" + brand.getName().replaceAll("\\s+", "_") + ".pdf";
                contentType = "application/pdf";
            } else if ("excel".equals(format)) {
                outputStream = catalogExportService.exportToExcel(enrichedProducts, "Каталог " + brand.getName());
                filename = "catalog_" + brand.getName().replaceAll("\\s+", "_") + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                return errorResponse("Invalid format. Use 'pdf' or 'excel'", HttpStatus.BAD_REQUEST);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating brand catalog: {}", e.getMessage());
            return errorResponse("Failed to generate brand catalog: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download sale products catalog (PDF or Excel)
     * @param format Format to download (pdf or excel)
     * @return Catalog file
     */
    @GetMapping("/catalogs/download/sale")
    public ResponseEntity<?> downloadSaleCatalog(@RequestParam String format) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            List<ProductDTO> saleProducts = productService.findProductsOnSale();

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = saleProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .collect(Collectors.toList());

            ByteArrayOutputStream outputStream;
            String filename;
            String contentType;

            if ("pdf".equals(format)) {
                outputStream = catalogExportService.exportToPdf(enrichedProducts, "Каталог акционных товаров", cosmetologist);
                filename = "sale_catalog.pdf";
                contentType = "application/pdf";
            } else if ("excel".equals(format)) {
                outputStream = catalogExportService.exportToExcel(enrichedProducts, "Каталог акционных товаров");
                filename = "sale_catalog.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                return errorResponse("Invalid format. Use 'pdf' or 'excel'", HttpStatus.BAD_REQUEST);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating sale catalog: {}", e.getMessage());
            return errorResponse("Failed to generate sale catalog: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download custom catalog (PDF or Excel)
     * @param format Format to download (pdf or excel)
     * @param categoryIds List of category IDs to filter by
     * @param brandIds List of brand IDs to filter by
     * @param onSale Filter for products on sale
     * @return Catalog file
     */
    @PostMapping("/catalogs/download/custom")
    public ResponseEntity<?> downloadCustomCatalog(
            @RequestParam String format,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) Boolean onSale) {

        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            CatalogFilterDTO filter = CatalogFilterDTO.builder()
                    .categoryIds(categoryIds)
                    .brandIds(brandIds)
                    .onSale(onSale)
                    .build();

            List<ProductDTO> products = productService.findProductsWithCustomFilters(filter);

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = products.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .collect(Collectors.toList());

            String catalogTitle = "Пользовательский каталог";
            ByteArrayOutputStream outputStream;
            String filename;
            String contentType;

            if ("pdf".equals(format)) {
                outputStream = catalogExportService.exportToPdf(enrichedProducts, catalogTitle, cosmetologist);
                filename = "custom_catalog.pdf";
                contentType = "application/pdf";
            } else if ("excel".equals(format)) {
                outputStream = catalogExportService.exportToExcel(enrichedProducts, catalogTitle);
                filename = "custom_catalog.xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                return errorResponse("Invalid format. Use 'pdf' or 'excel'", HttpStatus.BAD_REQUEST);
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating custom catalog: {}", e.getMessage());
            return errorResponse("Failed to generate custom catalog: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get cosmetologist's reviews
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of reviews
     */
    @GetMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewDTO> reviews = reviewService.findByUserIdOrderByCreatedAtDesc(cosmetologist.getId(), pageable);

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("reviews", reviews.getContent());
            data.put("currentPage", reviews.getNumber());
            data.put("totalItems", reviews.getTotalElements());
            data.put("totalPages", reviews.getTotalPages());
            
            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting reviews: {}", e.getMessage());
            return errorResponse("Failed to get reviews: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add or edit a review
     * @param reviewDTO Review data
     * @return Saved review
     */
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> saveReview(@RequestBody ReviewDTO reviewDTO) {
        String email = getCurrentUserEmail();
        
        // If user is not authenticated, return error
        if (email == null) {
            return errorResponse("Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Get cosmetologist from email
            UserDTO cosmetologist = getCurrentCosmetologist(email);
            if (cosmetologist == null) {
                return errorResponse("Cosmetologist access required", HttpStatus.FORBIDDEN);
            }

            // Set the user ID in the review
            reviewDTO.setUserId(cosmetologist.getId());
            
            // Save the review
            ReviewDTO savedReview = reviewService.save(reviewDTO);
            
            return successResponse(savedReview, "Review saved successfully");
        } catch (Exception e) {
            log.error("Error saving review: {}", e.getMessage());
            return errorResponse("Failed to save review: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to get cosmetologist from email
     * @param email The user's email
     * @return The cosmetologist user or null if not found or not a cosmetologist
     */
    private UserDTO getCurrentCosmetologist(String email) {
        Optional<UserDTO> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty() ||
                (!"COSMETOLOGIST".equals(userOpt.get().getRole()) && !"ADMIN".equals(userOpt.get().getRole()))) {
            return null;
        }
        return userOpt.get();
    }
}