package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.NewsDTO;
import kz.arannati.arannati.entity.News;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.NewsRepository;
import kz.arannati.arannati.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public NewsDTO convertToDto(News news) {
        if (news == null) {
            return null;
        }

        NewsDTO.NewsDTOBuilder builder = NewsDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .previewText(news.getPreviewText())
                .imagePath(news.getImagePath())
                .isPublished(news.isPublished())
                .isFeatured(news.isFeatured())
                .slug(news.getSlug())
                .metaTitle(news.getMetaTitle())
                .metaDescription(news.getMetaDescription())
                .sortOrder(news.getSortOrder())
                .viewsCount(news.getViewsCount())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .publishedAt(news.getPublishedAt());

        if (news.getAuthor() != null) {
            builder.authorId(news.getAuthor().getId());
        }

        return builder.build();
    }

    @Override
    public News convertToEntity(NewsDTO newsDTO) {
        if (newsDTO == null) {
            return null;
        }

        News news = new News();
        news.setId(newsDTO.getId());
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setPreviewText(newsDTO.getPreviewText());
        news.setImagePath(newsDTO.getImagePath());
        news.setPublished(newsDTO.isPublished());
        news.setFeatured(newsDTO.isFeatured());
        news.setSlug(newsDTO.getSlug());
        news.setMetaTitle(newsDTO.getMetaTitle());
        news.setMetaDescription(newsDTO.getMetaDescription());
        news.setSortOrder(newsDTO.getSortOrder());
        news.setViewsCount(newsDTO.getViewsCount());
        news.setCreatedAt(newsDTO.getCreatedAt());
        news.setUpdatedAt(newsDTO.getUpdatedAt());
        news.setPublishedAt(newsDTO.getPublishedAt());

        // Author relationship is typically handled separately
        // as it requires fetching the actual entity from the database

        return news;
    }

    @Override
    public Page<NewsDTO> findByPublishedIsTrueOrderByPublishedAtDesc(Pageable pageable) {
        Page<News> newsPage = newsRepository.findByPublishedIsTrueOrderByPublishedAtDesc(pageable);
        List<NewsDTO> newsDTOs = newsPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(newsDTOs, pageable, newsPage.getTotalElements());
    }

    @Override
    public Optional<NewsDTO> findByIdAndPublishedIsTrue(Long id) {
        return newsRepository.findByIdAndPublishedIsTrue(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<NewsDTO> findBySlugAndPublishedIsTrue(String slug) {
        return newsRepository.findBySlugAndPublishedIsTrue(slug)
                .map(this::convertToDto);
    }

    @Override
    public List<NewsDTO> findTop3ByPublishedIsTrueOrderByPublishedAtDesc() {
        return newsRepository.findTop3ByPublishedIsTrueOrderByPublishedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc() {
        return newsRepository.findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NewsDTO> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<NewsDTO> newsDTOs = newsPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(newsDTOs, pageable, newsPage.getTotalElements());
    }

    @Override
    public Page<NewsDTO> searchPublishedNews(String search, Pageable pageable) {
        Page<News> newsPage = newsRepository.searchPublishedNews(search, pageable);
        List<NewsDTO> newsDTOs = newsPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(newsDTOs, pageable, newsPage.getTotalElements());
    }

    @Override
    public Page<NewsDTO> searchAllNews(String search, Pageable pageable) {
        Page<News> newsPage = newsRepository.searchAllNews(search, pageable);
        List<NewsDTO> newsDTOs = newsPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(newsDTOs, pageable, newsPage.getTotalElements());
    }

    @Override
    public void incrementViewsCount(Long id) {
        newsRepository.incrementViewsCount(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return newsRepository.existsBySlug(slug);
    }

    @Override
    public long countByPublishedIsTrue() {
        return newsRepository.countByPublishedIsTrue();
    }

    @Override
    public long countByAuthorId(Long authorId) {
        return newsRepository.countByAuthorId(authorId);
    }

    @Override
    public NewsDTO save(NewsDTO newsDTO) {
        News news = convertToEntity(newsDTO);

        // Handle author relationship if authorId is provided
        if (newsDTO.getAuthorId() != null) {
            User author = new User();
            author.setId(newsDTO.getAuthorId());
            news.setAuthor(author);
        }

        News savedNews = newsRepository.save(news);
        return convertToDto(savedNews);
    }

    @Override
    public Optional<NewsDTO> findById(Long id) {
        return newsRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public List<NewsDTO> findAll() {
        return newsRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}