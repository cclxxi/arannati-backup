package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIdIsNullAndActiveIsTrueOrderBySortOrderAscNameAsc();

    List<Category> findByParentIdAndActiveIsTrueOrderBySortOrderAscNameAsc(Long parentId);

    Optional<Category> findByIdAndActiveIsTrue(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndParentId(String name, Long parentId);

    @Query("SELECT c FROM Category c WHERE c.active = true AND c.parent IS NULL")
    Page<Category> findMainCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true")
    Page<Category> findSubcategories(@Param("parentId") Long parentId, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.active = true AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Category> searchActiveCategories(@Param("search") String search);

    long countByParentIdIsNullAndActiveIsTrue();

    long countByParentIdAndActiveIsTrue(Long parentId);
}
