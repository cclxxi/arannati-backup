package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByPublishedIsTrueOrderByPublishedAtDesc(Pageable pageable);

    Optional<News> findByIdAndPublishedIsTrue(Long id);

    Optional<News> findBySlugAndPublishedIsTrue(String slug);

    List<News> findTop3ByPublishedIsTrueOrderByPublishedAtDesc();

    List<News> findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc();

    Page<News> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.published = true AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<News> searchPublishedNews(@Param("search") String search, Pageable pageable);

    @Query("SELECT n FROM News n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<News> searchAllNews(@Param("search") String search, Pageable pageable);

    @Modifying
    @Query("UPDATE News n SET n.viewsCount = n.viewsCount + 1 WHERE n.id = :id")
    void incrementViewsCount(@Param("id") Long id);

    boolean existsBySlug(String slug);

    long countByPublishedIsTrue();

    long countByAuthorId(Long authorId);
}
