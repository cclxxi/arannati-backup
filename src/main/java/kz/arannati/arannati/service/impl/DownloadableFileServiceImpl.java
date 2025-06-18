package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.DownloadableFile;
import kz.arannati.arannati.repository.DownloadableFileRepository;
import kz.arannati.arannati.service.DownloadableFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DownloadableFileServiceImpl implements DownloadableFileService {

    private final DownloadableFileRepository downloadableFileRepository;

    @Autowired
    public DownloadableFileServiceImpl(DownloadableFileRepository downloadableFileRepository) {
        this.downloadableFileRepository = downloadableFileRepository;
    }

    @Override
    public Page<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel, Pageable pageable) {
        return null;
    }

    @Override
    public List<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel) {
        return null;
    }

    @Override
    public Page<DownloadableFile> findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(String category, Pageable pageable) {
        return null;
    }

    @Override
    public List<DownloadableFile> findAccessibleFiles(boolean isCosmetologist) {
        return null;
    }

    @Override
    public Page<DownloadableFile> searchFiles(String search, Pageable pageable) {
        return null;
    }

    @Override
    public List<String> findDistinctCategories() {
        return null;
    }

    @Override
    public void incrementDownloadCount(Long id) {
        
    }

    @Override
    public long countByAccessLevelAndActiveIsTrue(String accessLevel) {
        return 0;
    }

    @Override
    public long countByCategoryAndActiveIsTrue(String category) {
        return 0;
    }

    @Override
    public DownloadableFile save(DownloadableFile downloadableFile) {
        return null;
    }

    @Override
    public Optional<DownloadableFile> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<DownloadableFile> findAll() {
        return null;
    }
}