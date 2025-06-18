package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Product;
import kz.arannati.arannati.repository.ProductRepository;
import kz.arannati.arannati.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Optional<Product> findByIdAndActiveIsTrue(Long id) {
        return null;
    }

    @Override
    public Optional<Product> findBySkuAndActiveIsTrue(String sku) {
        return null;
    }

    @Override
    public Page<Product> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findByCategoryIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long categoryId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findByBrandIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long brandId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findRegularProducts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findProfessionalProducts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> searchProducts(String search, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findProductsWithFilters(Long categoryId, Long brandId, Boolean isProfessional, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findNewProducts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Product> findProductsOnSale(Pageable pageable) {
        return null;
    }

    @Override
    public List<Product> findTop8ByActiveIsTrueOrderByCreatedAtDesc() {
        return null;
    }

    @Override
    public List<Product> findSimilarProducts(Long categoryId, Long excludeId, int limit) {
        return null;
    }

    @Override
    public boolean existsBySku(String sku) {
        return false;
    }

    @Override
    public long countByActiveIsTrue() {
        return 0;
    }

    @Override
    public long countByCategoryIdAndActiveIsTrue(Long categoryId) {
        return 0;
    }

    @Override
    public long countByBrandIdAndActiveIsTrue(Long brandId) {
        return 0;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<Product> findAll() {
        return null;
    }
}