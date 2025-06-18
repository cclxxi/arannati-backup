package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.BrandDTO;
import kz.arannati.arannati.entity.Brand;
import kz.arannati.arannati.repository.BrandRepository;
import kz.arannati.arannati.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    @Override
    public BrandDTO convertToDto(Brand brand) {
        if (brand == null) {
            return null;
        }
        
        return BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .logoPath(brand.getLogoPath())
                .country(brand.getCountry())
                .websiteUrl(brand.getWebsiteUrl())
                .isActive(brand.isActive())
                .sortOrder(brand.getSortOrder())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
    
    @Override
    public Brand convertToEntity(BrandDTO brandDTO) {
        if (brandDTO == null) {
            return null;
        }
        
        Brand brand = new Brand();
        brand.setId(brandDTO.getId());
        brand.setName(brandDTO.getName());
        brand.setDescription(brandDTO.getDescription());
        brand.setLogoPath(brandDTO.getLogoPath());
        brand.setCountry(brandDTO.getCountry());
        brand.setWebsiteUrl(brandDTO.getWebsiteUrl());
        brand.setActive(brandDTO.isActive());
        brand.setSortOrder(brandDTO.getSortOrder());
        brand.setCreatedAt(brandDTO.getCreatedAt());
        brand.setUpdatedAt(brandDTO.getUpdatedAt());
        
        return brand;
    }

    @Override
    public List<BrandDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc() {
        return brandRepository.findByActiveIsTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BrandDTO> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable) {
        Page<Brand> brandPage = brandRepository.findByActiveIsTrueOrderBySortOrderAscNameAsc(pageable);
        List<BrandDTO> brandDTOs = brandPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(brandDTOs, pageable, brandPage.getTotalElements());
    }

    @Override
    public Optional<BrandDTO> findByNameAndActiveIsTrue(String name) {
        return brandRepository.findByNameAndActiveIsTrue(name)
                .map(this::convertToDto);
    }

    @Override
    public boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }

    @Override
    public List<BrandDTO> searchActiveBrands(String search) {
        return brandRepository.searchActiveBrands(search)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countByActiveIsTrue() {
        return brandRepository.countByActiveIsTrue();
    }

    @Override
    public BrandDTO save(BrandDTO brandDTO) {
        Brand brand = convertToEntity(brandDTO);
        Brand savedBrand = brandRepository.save(brand);
        return convertToDto(savedBrand);
    }

    @Override
    public Optional<BrandDTO> findById(Long id) {
        return brandRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }

    @Override
    public List<BrandDTO> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BrandDTO> findAll(Pageable pageable) {
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        List<BrandDTO> brandDTOs = brandPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(brandDTOs, pageable, brandPage.getTotalElements());
    }
}