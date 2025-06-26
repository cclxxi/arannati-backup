package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing Material entities
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    /**
     * Find materials ordered by upload date (newest first)
     * @return List of materials
     */
    List<Material> findAllByOrderByUploadDateDesc();

    /**
     * Find materials uploaded by a specific user
     * @param uploadedBy Username of the uploader
     * @return List of materials
     */
    List<Material> findByUploadedByOrderByUploadDateDesc(String uploadedBy);
}