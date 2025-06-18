package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc();

    Page<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Optional<Brand> findByNameAndActiveIsTrue(String name);

    boolean existsByName(String name);

    List<Brand> searchActiveBrands(String search);

    long countByActiveIsTrue();
    
    Brand save(Brand brand);
    
    Optional<Brand> findById(Long id);
    
    void deleteById(Long id);
    
    List<Brand> findAll();
    
    Page<Brand> findAll(Pageable pageable);
}