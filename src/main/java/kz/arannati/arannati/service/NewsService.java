package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.NewsDTO;
import kz.arannati.arannati.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NewsService {

    Page<NewsDTO> findByPublishedIsTrueOrderByPublishedAtDesc(Pageable pageable);

    Optional<NewsDTO> findByIdAndPublishedIsTrue(Long id);

    Optional<NewsDTO> findBySlugAndPublishedIsTrue(String slug);

    List<NewsDTO> findTop3ByPublishedIsTrueOrderByPublishedAtDesc();

    List<NewsDTO> findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc();

    Page<NewsDTO> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<NewsDTO> searchPublishedNews(String search, Pageable pageable);

    Page<NewsDTO> searchAllNews(String search, Pageable pageable);

    void incrementViewsCount(Long id);

    boolean existsBySlug(String slug);

    long countByPublishedIsTrue();

    long countByAuthorId(Long authorId);

    NewsDTO save(NewsDTO newsDTO);

    Optional<NewsDTO> findById(Long id);

    void deleteById(Long id);

    List<NewsDTO> findAll();

    NewsDTO convertToDto(News news);

    News convertToEntity(NewsDTO newsDTO);
}
