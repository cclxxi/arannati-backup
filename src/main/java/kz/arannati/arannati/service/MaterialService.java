package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Material;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling material operations
 */
public interface MaterialService {

    /**
     * Save a material file and its metadata
     * @param file The file to save
     * @param title The title of the material
     * @param description The description of the material
     * @param uploadedBy The username of the uploader
     * @return The saved material
     * @throws IOException If an I/O error occurs
     */
    Material saveMaterial(MultipartFile file, String title, String description, String uploadedBy) throws IOException;

    /**
     * Get a material by its ID
     * @param id The ID of the material
     * @return The material, if found
     */
    Optional<Material> findById(Long id);

    /**
     * Get all materials ordered by upload date (newest first)
     * @return List of materials
     */
    List<Material> findAllByOrderByUploadDateDesc();

    /**
     * Get materials uploaded by a specific user
     * @param uploadedBy Username of the uploader
     * @return List of materials
     */
    List<Material> findByUploadedBy(String uploadedBy);

    /**
     * Delete a material by its ID
     * @param id The ID of the material to delete
     * @return true if the material was deleted successfully
     */
    boolean deleteById(Long id);
}