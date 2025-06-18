package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Brand;
import kz.arannati.arannati.repository.BrandRepository;
import kz.arannati.arannati.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc() {
        return null;
    }

    @Override
    public Page<Brand> findByActiveIsTrueOrderBySortOrderAscNameAsc(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Brand> findByNameAndActiveIsTrue(String name) {
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public List<Brand> searchActiveBrands(String search) {
        return null;
    }

    @Override
    public long countByActiveIsTrue() {
        return 0;
    }

    @Override
    public Brand save(Brand brand) {
        return null;
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<Brand> findAll() {
        return null;
    }

    @Override
    public Page<Brand> findAll(Pageable pageable) {
        return null;
    }
}