package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    List<Brand> findByActiveTrueOrderBySortOrderAscNameAsc();

    Page<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Optional<Brand> findByNameAndActiveIsTrue(String name);

    boolean existsByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.isActive = true AND " +
            "LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Brand> searchActiveBrands(String search);

    long countByActiveIsTrue();
}