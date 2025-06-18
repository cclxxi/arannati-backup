package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc();

    List<Category> findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long parentId);

    Optional<Category> findByIdAndActiveIsTrue(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndParentId(String name, Long parentId);

    Page<Category> findMainCategories(Pageable pageable);

    Page<Category> findSubcategories(Long parentId, Pageable pageable);

    List<Category> searchActiveCategories(String search);

    long countByParentIdIsNullAndActiveIsTrue();

    long countByParentIdAndActiveIsTrue(Long parentId);
    
    Category save(Category category);
    
    Optional<Category> findById(Long id);
    
    void deleteById(Long id);
    
    List<Category> findAll();
    
    Page<Category> findAll(Pageable pageable);
}