package kz.arannati.arannati.controller;

import kz.arannati.arannati.entity.Material;
import kz.arannati.arannati.service.FileStorageService;
import kz.arannati.arannati.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Controller for handling material downloads
 */
@Slf4j
@Controller
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {

    private static final String MATERIALS_DIRECTORY = "materials";

    private final MaterialService materialService;
    private final FileStorageService fileStorageService;

    /**
     * Download a material file
     * @param id The ID of the material to download
     * @return The file as a response entity
     */
    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COSMETOLOGIST')")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        try {
            // Get the material
            Optional<Material> materialOpt = materialService.findById(id);
            if (materialOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Material material = materialOpt.get();

            // Get the file path
            Path filePath = fileStorageService.getFilePath(material.getFilePath(), MATERIALS_DIRECTORY);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                log.error("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = material.getFileType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Return the file
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading material: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
