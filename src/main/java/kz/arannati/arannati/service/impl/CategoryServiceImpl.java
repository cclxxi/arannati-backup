package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Category;
import kz.arannati.arannati.repository.CategoryRepository;
import kz.arannati.arannati.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc() {
        return null;
    }

    @Override
    public List<Category> findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long parentId) {
        return null;
    }

    @Override
    public Optional<Category> findByIdAndActiveIsTrue(Long id) {
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public boolean existsByNameAndParentId(String name, Long parentId) {
        return false;
    }

    @Override
    public Page<Category> findMainCategories(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Category> findSubcategories(Long parentId, Pageable pageable) {
        return null;
    }

    @Override
    public List<Category> searchActiveCategories(String search) {
        return null;
    }

    @Override
    public long countByParentIdIsNullAndActiveIsTrue() {
        return 0;
    }

    @Override
    public long countByParentIdAndActiveIsTrue(Long parentId) {
        return 0;
    }

    @Override
    public Category save(Category category) {
        return null;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<Category> findAll() {
        return null;
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return null;
    }
}