package kz.arannati.arannati.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller for handling product catalog functionality
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    /**
     * Root page - Product catalog
     */
    @GetMapping("/")
    public String catalog(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Boolean professional,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "sortOrder,asc") String[] sort,
            Model model) {

        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
                                 !"anonymousUser".equals(authentication.getPrincipal());

        // Check if user is a cosmetologist
        boolean isCosmetologist = isAuthenticated && 
                                 authentication.getAuthorities().stream()
                                 .anyMatch(a -> a.getAuthority().equals("ROLE_COSMETOLOGIST"));

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
            model.addAttribute("search", search);
        } else if (categoryId != null || brandId != null || professional != null || minPrice != null || maxPrice != null) {
            products = productService.findProductsWithFilters(categoryId, brandId, professional, minPrice, maxPrice, pageable);
        } else {
            products = productService.findByActiveIsTrueOrderBySortOrderAscNameAsc(pageable);
        }

        // Add data to model
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc());
        model.addAttribute("brands", brandService.findByActiveIsTrueOrderBySortOrderAscNameAsc());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("professional", professional);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isCosmetologist", isCosmetologist);

        return "catalog/index";
    }

    /**
     * Product details page
     */
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && 
                                 !"anonymousUser".equals(authentication.getPrincipal());

        // Check if user is a cosmetologist
        boolean isCosmetologist = isAuthenticated && 
                                 authentication.getAuthorities().stream()
                                 .anyMatch(a -> a.getAuthority().equals("ROLE_COSMETOLOGIST"));

        // Get product
        Optional<ProductDTO> productOpt = productService.findByIdAndActiveIsTrue(id);
        if (productOpt.isEmpty()) {
            return "redirect:/";
        }

        ProductDTO product = productOpt.get();

        // Get similar products
        var similarProducts = productService.findSimilarProducts(product.getCategoryId(), product.getId(), 4);

        // Add data to model
        model.addAttribute("product", product);
        model.addAttribute("similarProducts", similarProducts);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isCosmetologist", isCosmetologist);

        return "catalog/product-details";
    }
}
