package kz.arannati.arannati.controller.api;

import kz.arannati.arannati.dto.ProductDTO;
import kz.arannati.arannati.service.BrandService;
import kz.arannati.arannati.service.CategoryService;
import kz.arannati.arannati.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST API controller for product catalog functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogApiController extends BaseApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    /**
     * Get products with optional filtering
     * @param categoryId Optional category ID filter
     * @param brandId Optional brand ID filter
     * @param professional Optional professional-only filter
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param search Optional search term
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sorting parameters (field,direction)
     * @return List of products matching the criteria
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Boolean professional,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "sortOrder,asc") String[] sort) {
        
        try {
            // Create pageable object for pagination
            String sortField = sort[0];
            String sortDirection = sort.length > 1 ? sort[1] : "asc";
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sortObj = Sort.by(direction, sortField);
            Pageable pageable = PageRequest.of(page, size, sortObj);

            // Get products based on filters
            Page<ProductDTO> products;
            if (search != null && !search.isEmpty()) {
                products = productService.searchProducts(search, pageable);
            } else if (categoryId != null || brandId != null || professional != null || minPrice != null || maxPrice != null) {
                products = productService.findProductsWithFilters(categoryId, brandId, professional, minPrice, maxPrice, pageable);
            } else {
                products = productService.findByActiveIsTrueOrderBySortOrderAscNameAsc(pageable);
            }

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("products", products.getContent());
            data.put("currentPage", products.getNumber());
            data.put("totalItems", products.getTotalElements());
            data.put("totalPages", products.getTotalPages());
            
            // Add filter data
            data.put("categories", categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc());
            data.put("brands", brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc());
            
            // Add filter parameters to response
            if (categoryId != null) data.put("categoryId", categoryId);
            if (brandId != null) data.put("brandId", brandId);
            if (professional != null) data.put("professional", professional);
            if (minPrice != null) data.put("minPrice", minPrice);
            if (maxPrice != null) data.put("maxPrice", maxPrice);
            if (search != null) data.put("search", search);

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting products: {}", e.getMessage(), e);
            return errorResponse("Failed to retrieve products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get product details by ID
     * @param id Product ID
     * @return Product details
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProductDetails(@PathVariable Long id) {
        try {
            Optional<ProductDTO> productOpt = productService.findByIdAndActiveIsTrue(id);
            if (productOpt.isEmpty()) {
                return errorResponse("Product not found", HttpStatus.NOT_FOUND);
            }

            ProductDTO product = productOpt.get();
            
            // Get similar products
            var similarProducts = productService.findSimilarProducts(product.getCategoryId(), product.getId(), 4);
            
            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("product", product);
            data.put("similarProducts", similarProducts);
            
            // Add user-specific data if authenticated
            String userEmail = getCurrentUserEmail();
            if (userEmail != null) {
                data.put("isCosmetologist", hasRole("COSMETOLOGIST"));
            }

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting product details: {}", e.getMessage(), e);
            return errorResponse("Failed to retrieve product details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}