package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.DownloadableFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadableFileRepository extends JpaRepository<DownloadableFile, Long> {

    Page<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel, Pageable pageable);

    List<DownloadableFile> findByAccessLevelAndActiveIsTrueOrderBySortOrderAscTitleAsc(String accessLevel);

    Page<DownloadableFile> findByCategoryAndActiveIsTrueOrderBySortOrderAscTitleAsc(String category, Pageable pageable);

    @Query("SELECT df FROM DownloadableFile df WHERE df.active = true AND " +
            "(df.accessLevel = 'PUBLIC' OR " +
            "(df.accessLevel = 'COSMETOLOGIST_ONLY' AND :isCosmetologist = true)) " +
            "ORDER BY df.sortOrder ASC, df.title ASC")
    List<DownloadableFile> findAccessibleFiles(@Param("isCosmetologist") boolean isCosmetologist);

    @Query("SELECT df FROM DownloadableFile df WHERE df.active = true AND " +
            "LOWER(df.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<DownloadableFile> searchFiles(@Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT df.category FROM DownloadableFile df WHERE df.category IS NOT NULL AND df.active = true")
    List<String> findDistinctCategories();

    @Modifying
    @Query("UPDATE DownloadableFile df SET df.downloadCount = df.downloadCount + 1 WHERE df.id = :id")
    void incrementDownloadCount(@Param("id") Long id);

    long countByAccessLevelAndActiveIsTrue(String accessLevel);

    long countByCategoryAndActiveIsTrue(String category);
}
