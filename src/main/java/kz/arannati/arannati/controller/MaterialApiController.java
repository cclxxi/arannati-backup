package kz.arannati.arannati.controller;

import kz.arannati.arannati.entity.Material;
import kz.arannati.arannati.service.FileStorageService;
import kz.arannati.arannati.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST API controller for material functionality
 */
@Slf4j
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialApiController extends BaseApiController {

    private static final String MATERIALS_DIRECTORY = "materials";

    private final MaterialService materialService;
    private final FileStorageService fileStorageService;

    /**
     * Get material details by ID
     * @param id Material ID
     * @return Material details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMaterial(@PathVariable Long id) {
        try {
            Optional<Material> materialOpt = materialService.findById(id);
            if (materialOpt.isEmpty()) {
                return errorResponse("Material not found", HttpStatus.NOT_FOUND);
            }

            Material material = materialOpt.get();

            // Create response data
            Map<String, Object> data = new HashMap<>();
            data.put("id", material.getId());
            data.put("title", material.getTitle());
            data.put("description", material.getDescription());
            data.put("fileName", material.getFileName());
            data.put("fileType", material.getFileType());
            data.put("fileSize", material.getFileSize());
            data.put("filePath", material.getFilePath());
            data.put("uploadDate", material.getUploadDate());
            data.put("uploadedBy", material.getUploadedBy());
            data.put("downloadUrl", "/api/materials/download/" + material.getId());

            return successResponse(data);
        } catch (Exception e) {
            log.error("Error getting material: {}", e.getMessage(), e);
            return errorResponse("Failed to get material: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download a material file
     * @param id The ID of the material to download
     * @return The file as a response entity
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadMaterial(@PathVariable Long id) {
        try {
            // Get the material
            Optional<Material> materialOpt = materialService.findById(id);
            if (materialOpt.isEmpty()) {
                return errorResponse("Material not found", HttpStatus.NOT_FOUND);
            }

            Material material = materialOpt.get();

            // Get the file path
            Path filePath = fileStorageService.getFilePath(material.getFilePath(), MATERIALS_DIRECTORY);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                log.error("File not found: {}", filePath);
                return errorResponse("File not found", HttpStatus.NOT_FOUND);
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
            return errorResponse("Failed to download material: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
