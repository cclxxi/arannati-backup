package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.*;
import kz.arannati.arannati.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/cosmetologist")
@RequiredArgsConstructor
public class CosmetologistController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final CatalogExportService catalogExportService;
    private final PricingService pricingService;

    /**
     * Cosmetologist dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return "redirect:/auth/login";
        }

        // Recent orders
        Page<OrderDTO> recentOrders = orderService.findByUserIdOrderByCreatedAtDesc(
                cosmetologist.getId(),
                PageRequest.of(0, 5)
        );

        // Catalog statistics
        long totalProducts = productService.countByActiveIsTrue();
        long saleProducts = productService.countProductsOnSale();

        model.addAttribute("currentUser", cosmetologist);
        model.addAttribute("recentOrders", recentOrders.getContent());
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("saleProducts", saleProducts);

        return "cosmetologist/dashboard";
    }

    /**
     * Professional catalog page with special prices
     */
    @GetMapping("/catalog")
    public String professionalCatalog(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort,
            Model model) {

        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return "redirect:/auth/login";
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
                .toList();

        model.addAttribute("products", products);
        model.addAttribute("enrichedProducts", enrichedProducts);
        model.addAttribute("categories", categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc());
        model.addAttribute("brands", brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("search", search);
        model.addAttribute("currentUser", cosmetologist);

        return "cosmetologist/catalog";
    }

    /**
     * Download catalogs page
     */
    @GetMapping("/catalogs")
    public String catalogsPage(Model model) {
        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return "redirect:/auth/login";
        }

        List<CategoryDTO> categories = categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc();
        List<BrandDTO> brands = brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc();

        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", cosmetologist);

        return "cosmetologist/catalogs";
    }

    /**
     * Download full catalog (PDF)
     */
    @GetMapping("/catalogs/download/full")
    public ResponseEntity<Resource> downloadFullCatalog(@RequestParam String format) {
        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductDTO> allProducts = productService.findAllActiveProducts();

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = allProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .toList();

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
                return ResponseEntity.badRequest().build();
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating catalog", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download catalog by brand
     */
    @GetMapping("/catalogs/download/brand/{brandId}")
    public ResponseEntity<Resource> downloadBrandCatalog(
            @PathVariable Long brandId,
            @RequestParam String format) {

        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Optional<BrandDTO> brandOpt = brandService.findById(brandId);
            if (brandOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            BrandDTO brand = brandOpt.get();
            List<ProductDTO> brandProducts = productService.findByBrandIdAndActiveIsTrue(brandId);

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = brandProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .toList();

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
                return ResponseEntity.badRequest().build();
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating brand catalog", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download sale products catalog
     */
    @GetMapping("/catalogs/download/sale")
    public ResponseEntity<Resource> downloadSaleCatalog(@RequestParam String format) {
        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ProductDTO> saleProducts = productService.findProductsOnSale();

            // Enrich with cosmetologist pricing
            List<ProductDTO> enrichedProducts = saleProducts.stream()
                    .map(product -> {
                        product.setEffectivePrice(pricingService.getEffectivePrice(product, cosmetologist));
                        return product;
                    })
                    .toList();

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
                return ResponseEntity.badRequest().build();
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating sale catalog", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download custom catalog
     */
    @PostMapping("/catalogs/download/custom")
    public ResponseEntity<Resource> downloadCustomCatalog(
            @RequestParam String format,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) Boolean onSale) {

        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
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
                    .toList();

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
                return ResponseEntity.badRequest().build();
            }

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("Error generating custom catalog", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * My reviews page
     */
    @GetMapping("/reviews")
    public String myReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return "redirect:/auth/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviews = reviewService.findByUserIdOrderByCreatedAtDesc(cosmetologist.getId(), pageable);

        model.addAttribute("reviews", reviews);
        model.addAttribute("currentUser", cosmetologist);

        return "cosmetologist/reviews";
    }

    /**
     * Add/Edit review
     */
    @PostMapping("/api/reviews/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveReview(@RequestBody ReviewDTO reviewDTO) {
        UserDTO cosmetologist = getCurrentCosmetologist();
        if (cosmetologist == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Необходимо войти в систему"));
        }

        try {
            reviewDTO.setUserId(cosmetologist.getId());
            ReviewDTO savedReview = reviewService.save(reviewDTO);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Отзыв сохранен",
                    "review", savedReview
            ));

        } catch (Exception e) {
            log.error("Error saving review", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Ошибка сохранения отзыва"));
        }
    }

    private UserDTO getCurrentCosmetologist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        Optional<UserDTO> userOpt = userService.findByEmail(auth.getName());
        if (userOpt.isEmpty() ||
                (!"COSMETOLOGIST".equals(userOpt.get().getRole()) && !"ADMIN".equals(userOpt.get().getRole()))) {
            return null;
        }

        return userOpt.get();
    }
}