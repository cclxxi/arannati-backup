package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.DownloadableFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DownloadableFileService {

    Page<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel, Pageable pageable);

    List<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel);

    Page<DownloadableFile> findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(String category, Pageable pageable);

    List<DownloadableFile> findAccessibleFiles(boolean isCosmetologist);

    Page<DownloadableFile> searchFiles(String search, Pageable pageable);

    List<String> findDistinctCategories();

    void incrementDownloadCount(Long id);

    long countByAccessLevelAndActiveIsTrue(String accessLevel);

    long countByCategoryAndActiveIsTrue(String category);
    
    DownloadableFile save(DownloadableFile downloadableFile);
    
    Optional<DownloadableFile> findById(Long id);
    
    void deleteById(Long id);
    
    List<DownloadableFile> findAll();
}