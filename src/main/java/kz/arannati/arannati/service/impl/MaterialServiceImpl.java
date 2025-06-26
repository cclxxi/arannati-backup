package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.Material;
import kz.arannati.arannati.repository.MaterialRepository;
import kz.arannati.arannati.service.FileStorageService;
import kz.arannati.arannati.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the MaterialService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private static final String MATERIALS_DIRECTORY = "materials";

    private final MaterialRepository materialRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Material saveMaterial(MultipartFile file, String title, String description, String uploadedBy) throws IOException {
        // Store the file
        String filePath = fileStorageService.storeFile(file, MATERIALS_DIRECTORY);

        // Create and save the material metadata
        Material material = Material.builder()
                .title(title)
                .description(description)
                .filePath(filePath)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadDate(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .build();

        log.info("Saving material: {}", material.getTitle());
        return materialRepository.save(material);
    }

    @Override
    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    @Override
    public List<Material> findAllByOrderByUploadDateDesc() {
        return materialRepository.findAllByOrderByUploadDateDesc();
    }

    @Override
    public List<Material> findByUploadedBy(String uploadedBy) {
        return materialRepository.findByUploadedByOrderByUploadDateDesc(uploadedBy);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            Optional<Material> materialOpt = materialRepository.findById(id);
            if (materialOpt.isPresent()) {
                Material material = materialOpt.get();

                // Delete the file from the file system
                boolean fileDeleted = fileStorageService.deleteFile(material.getFilePath(), MATERIALS_DIRECTORY);
                if (!fileDeleted) {
                    log.warn("Failed to delete file: {}", material.getFilePath());
                }

                // Delete the material metadata from the database
                materialRepository.deleteById(id);
                log.info("Deleted material: {}", material.getTitle());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting material with ID {}: {}", id, e.getMessage());
            return false;
        }
    }
}
