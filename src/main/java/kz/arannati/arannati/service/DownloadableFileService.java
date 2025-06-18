package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.DownloadableFileDTO;
import kz.arannati.arannati.entity.DownloadableFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DownloadableFileService {

    Page<DownloadableFileDTO> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel, Pageable pageable);

    List<DownloadableFileDTO> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel);

    Page<DownloadableFileDTO> findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(String category, Pageable pageable);

    List<DownloadableFileDTO> findAccessibleFiles(boolean isCosmetologist);

    Page<DownloadableFileDTO> searchFiles(String search, Pageable pageable);

    List<String> findDistinctCategories();

    void incrementDownloadCount(Long id);

    long countByAccessLevelAndActiveIsTrue(String accessLevel);

    long countByCategoryAndActiveIsTrue(String category);

    DownloadableFileDTO save(DownloadableFileDTO downloadableFileDTO);

    Optional<DownloadableFileDTO> findById(Long id);

    void deleteById(Long id);

    List<DownloadableFileDTO> findAll();

    DownloadableFileDTO convertToDto(DownloadableFile downloadableFile);

    DownloadableFile convertToEntity(DownloadableFileDTO downloadableFileDTO);
}
