package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NewsService {

    Page<News> findByPublishedIsTrueOrderByPublishedAtDesc(Pageable pageable);

    Optional<News> findByIdAndPublishedIsTrue(Long id);

    Optional<News> findBySlugAndPublishedIsTrue(String slug);

    List<News> findTop3ByPublishedIsTrueOrderByPublishedAtDesc();

    List<News> findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc();

    Page<News> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<News> searchPublishedNews(String search, Pageable pageable);

    Page<News> searchAllNews(String search, Pageable pageable);

    void incrementViewsCount(Long id);

    boolean existsBySlug(String slug);

    long countByPublishedIsTrue();

    long countByAuthorId(Long authorId);
    
    News save(News news);
    
    Optional<News> findById(Long id);
    
    void deleteById(Long id);
    
    List<News> findAll();
}