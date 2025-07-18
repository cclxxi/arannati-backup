package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @RestResource(path = "by-active-list", rel = "by-active-list")
    List<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc();

    @RestResource(path = "by-active-page", rel = "by-active-page")
    Page<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Optional<Brand> findByNameAndActiveIsTrue(String name);

    boolean existsByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.active = true AND " +
            "LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Brand> searchActiveBrands(String search);

    long countByActiveIsTrue();
}