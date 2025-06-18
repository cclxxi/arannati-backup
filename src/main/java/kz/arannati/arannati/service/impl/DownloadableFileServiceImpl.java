package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.DownloadableFileDTO;
import kz.arannati.arannati.entity.DownloadableFile;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.DownloadableFileRepository;
import kz.arannati.arannati.service.DownloadableFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DownloadableFileServiceImpl implements DownloadableFileService {

    private final DownloadableFileRepository downloadableFileRepository;

    @Autowired
    public DownloadableFileServiceImpl(DownloadableFileRepository downloadableFileRepository) {
        this.downloadableFileRepository = downloadableFileRepository;
    }

    @Override
    public DownloadableFileDTO convertToDto(DownloadableFile downloadableFile) {
        if (downloadableFile == null) {
            return null;
        }

        DownloadableFileDTO.DownloadableFileDTOBuilder builder = DownloadableFileDTO.builder()
                .id(downloadableFile.getId())
                .title(downloadableFile.getTitle())
                .description(downloadableFile.getDescription())
                .filePath(downloadableFile.getFilePath())
                .originalFilename(downloadableFile.getOriginalFilename())
                .fileSize(downloadableFile.getFileSize())
                .mimeType(downloadableFile.getMimeType())
                .accessLevel(downloadableFile.getAccessLevel())
                .category(downloadableFile.getCategory())
                .isActive(downloadableFile.isActive())
                .downloadCount(downloadableFile.getDownloadCount())
                .sortOrder(downloadableFile.getSortOrder())
                .createdAt(downloadableFile.getCreatedAt())
                .updatedAt(downloadableFile.getUpdatedAt());

        if (downloadableFile.getUploadedBy() != null) {
            builder.uploadedById(downloadableFile.getUploadedBy().getId());
        }

        return builder.build();
    }

    @Override
    public DownloadableFile convertToEntity(DownloadableFileDTO downloadableFileDTO) {
        if (downloadableFileDTO == null) {
            return null;
        }

        DownloadableFile downloadableFile = new DownloadableFile();
        downloadableFile.setId(downloadableFileDTO.getId());
        downloadableFile.setTitle(downloadableFileDTO.getTitle());
        downloadableFile.setDescription(downloadableFileDTO.getDescription());
        downloadableFile.setFilePath(downloadableFileDTO.getFilePath());
        downloadableFile.setOriginalFilename(downloadableFileDTO.getOriginalFilename());
        downloadableFile.setFileSize(downloadableFileDTO.getFileSize());
        downloadableFile.setMimeType(downloadableFileDTO.getMimeType());
        downloadableFile.setAccessLevel(downloadableFileDTO.getAccessLevel());
        downloadableFile.setCategory(downloadableFileDTO.getCategory());
        downloadableFile.setActive(downloadableFileDTO.isActive());
        downloadableFile.setDownloadCount(downloadableFileDTO.getDownloadCount());
        downloadableFile.setSortOrder(downloadableFileDTO.getSortOrder());
        downloadableFile.setCreatedAt(downloadableFileDTO.getCreatedAt());
        downloadableFile.setUpdatedAt(downloadableFileDTO.getUpdatedAt());

        // UploadedBy relationship is typically handled separately
        // as it requires fetching the actual entity from the database

        return downloadableFile;
    }

    @Override
    public Page<DownloadableFileDTO> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel, Pageable pageable) {
        Page<DownloadableFile> filePage = downloadableFileRepository.findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(accessLevel, pageable);
        List<DownloadableFileDTO> fileDTOs = filePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(fileDTOs, pageable, filePage.getTotalElements());
    }

    @Override
    public List<DownloadableFileDTO> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel) {
        return downloadableFileRepository.findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(accessLevel)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DownloadableFileDTO> findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(String category, Pageable pageable) {
        Page<DownloadableFile> filePage = downloadableFileRepository.findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(category, pageable);
        List<DownloadableFileDTO> fileDTOs = filePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(fileDTOs, pageable, filePage.getTotalElements());
    }

    @Override
    public List<DownloadableFileDTO> findAccessibleFiles(boolean isCosmetologist) {
        return downloadableFileRepository.findAccessibleFiles(isCosmetologist)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DownloadableFileDTO> searchFiles(String search, Pageable pageable) {
        Page<DownloadableFile> filePage = downloadableFileRepository.searchFiles(search, pageable);
        List<DownloadableFileDTO> fileDTOs = filePage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(fileDTOs, pageable, filePage.getTotalElements());
    }

    @Override
    public List<String> findDistinctCategories() {
        return downloadableFileRepository.findDistinctCategories();
    }

    @Override
    public void incrementDownloadCount(Long id) {
        downloadableFileRepository.incrementDownloadCount(id);
    }

    @Override
    public long countByAccessLevelAndActiveIsTrue(String accessLevel) {
        return downloadableFileRepository.countByAccessLevelAndActiveIsTrue(accessLevel);
    }

    @Override
    public long countByCategoryAndActiveIsTrue(String category) {
        return downloadableFileRepository.countByCategoryAndActiveIsTrue(category);
    }

    @Override
    public DownloadableFileDTO save(DownloadableFileDTO downloadableFileDTO) {
        DownloadableFile downloadableFile = convertToEntity(downloadableFileDTO);

        // Handle uploadedBy relationship if uploadedById is provided
        if (downloadableFileDTO.getUploadedById() != null) {
            User uploadedBy = new User();
            uploadedBy.setId(downloadableFileDTO.getUploadedById());
            downloadableFile.setUploadedBy(uploadedBy);
        }

        DownloadableFile savedFile = downloadableFileRepository.save(downloadableFile);
        return convertToDto(savedFile);
    }

    @Override
    public Optional<DownloadableFileDTO> findById(Long id) {
        return downloadableFileRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        downloadableFileRepository.deleteById(id);
    }

    @Override
    public List<DownloadableFileDTO> findAll() {
        return downloadableFileRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}