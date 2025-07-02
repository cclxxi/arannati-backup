package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.BrandDTO;
import kz.arannati.arannati.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrandService {

    List<BrandDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc();

    Page<BrandDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable);

    Optional<BrandDTO> findByNameAndActiveIsTrue(String name);

    boolean existsByName(String name);

    List<BrandDTO> searchActiveBrands(String search);

    long countByActiveIsTrue();

    BrandDTO save(BrandDTO brandDTO);

    Optional<BrandDTO> findById(Long id);

    void deleteById(Long id);

    List<BrandDTO> findAll();

    Page<BrandDTO> findAll(Pageable pageable);

    List<BrandDTO> findAllActive();

    BrandDTO convertToDto(Brand brand);

    Brand convertToEntity(BrandDTO brandDTO);
}
