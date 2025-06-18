package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.CategoryDTO;
import kz.arannati.arannati.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryDTO> findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc();

    List<CategoryDTO> findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long parentId);

    Optional<CategoryDTO> findByIdAndActiveIsTrue(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndParentId(String name, Long parentId);

    Page<CategoryDTO> findMainCategories(Pageable pageable);

    Page<CategoryDTO> findSubcategories(Long parentId, Pageable pageable);

    List<CategoryDTO> searchActiveCategories(String search);

    long countByParentIdIsNullAndActiveIsTrue();

    long countByParentIdAndActiveIsTrue(Long parentId);

    CategoryDTO save(CategoryDTO categoryDTO);

    Optional<CategoryDTO> findById(Long id);

    void deleteById(Long id);

    List<CategoryDTO> findAll();

    Page<CategoryDTO> findAll(Pageable pageable);

    CategoryDTO convertToDto(Category category);

    Category convertToEntity(CategoryDTO categoryDTO);
}
