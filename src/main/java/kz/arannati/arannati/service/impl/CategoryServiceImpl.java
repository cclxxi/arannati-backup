package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.CategoryDTO;
import kz.arannati.arannati.entity.Category;
import kz.arannati.arannati.repository.CategoryRepository;
import kz.arannati.arannati.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDTO convertToDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO.CategoryDTOBuilder builder = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imagePath(category.getImagePath())
                .isActive(category.isActive())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());

        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId());
        }

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            builder.childrenIds(category.getChildren().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    @Override
    public Category convertToEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setImagePath(categoryDTO.getImagePath());
        category.setActive(categoryDTO.isActive());
        category.setSortOrder(categoryDTO.getSortOrder());
        category.setCreatedAt(categoryDTO.getCreatedAt());
        category.setUpdatedAt(categoryDTO.getUpdatedAt());

        // Parent and children relationships are typically handled separately
        // as they require fetching the actual entities from the database

        return category;
    }

    @Override
    public List<CategoryDTO> findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc() {
        return categoryRepository.findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long parentId) {
        return categoryRepository.findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(parentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryDTO> findByIdAndActiveIsTrue(Long id) {
        return categoryRepository.findByIdAndActiveIsTrue(id)
                .map(this::convertToDto);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndParentId(String name, Long parentId) {
        return categoryRepository.existsByNameAndParentId(name, parentId);
    }

    @Override
    public Page<CategoryDTO> findMainCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findMainCategories(pageable);
        List<CategoryDTO> categoryDTOs = categoryPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(categoryDTOs, pageable, categoryPage.getTotalElements());
    }

    @Override
    public Page<CategoryDTO> findSubcategories(Long parentId, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findSubcategories(parentId, pageable);
        List<CategoryDTO> categoryDTOs = categoryPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(categoryDTOs, pageable, categoryPage.getTotalElements());
    }

    @Override
    public List<CategoryDTO> searchActiveCategories(String search) {
        return categoryRepository.searchActiveCategories(search)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countByParentIdIsNullAndActiveIsTrue() {
        return categoryRepository.countByParentIdIsNullAndActiveIsTrue();
    }

    @Override
    public long countByParentIdAndActiveIsTrue(Long parentId) {
        return categoryRepository.countByParentIdAndActiveIsTrue(parentId);
    }

    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);

        // Handle parent relationship if parentId is provided
        if (categoryDTO.getParentId() != null) {
            categoryRepository.findById(categoryDTO.getParentId())
                    .ifPresent(category::setParent);
        }

        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    @Override
    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryDTO> categoryDTOs = categoryPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(categoryDTOs, pageable, categoryPage.getTotalElements());
    }

    @Override
    public List<CategoryDTO> findAllActive() {
        return categoryRepository.findAll()
                .stream()
                .filter(Category::isActive)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
