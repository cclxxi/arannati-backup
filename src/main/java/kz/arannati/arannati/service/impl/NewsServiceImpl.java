package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.News;
import kz.arannati.arannati.repository.NewsRepository;
import kz.arannati.arannati.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public Page<News> findByPublishedIsTrueOrderByPublishedAtDesc(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<News> findByIdAndPublishedIsTrue(Long id) {
        return null;
    }

    @Override
    public Optional<News> findBySlugAndPublishedIsTrue(String slug) {
        return null;
    }

    @Override
    public List<News> findTop3ByPublishedIsTrueOrderByPublishedAtDesc() {
        return null;
    }

    @Override
    public List<News> findByPublishedIsTrueAndFeaturedIsTrueOrderBySortOrderAscPublishedAtDesc() {
        return null;
    }

    @Override
    public Page<News> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        return null;
    }

    @Override
    public Page<News> searchPublishedNews(String search, Pageable pageable) {
        return null;
    }

    @Override
    public Page<News> searchAllNews(String search, Pageable pageable) {
        return null;
    }

    @Override
    public void incrementViewsCount(Long id) {
        
    }

    @Override
    public boolean existsBySlug(String slug) {
        return false;
    }

    @Override
    public long countByPublishedIsTrue() {
        return 0;
    }

    @Override
    public long countByAuthorId(Long authorId) {
        return 0;
    }

    @Override
    public News save(News news) {
        return null;
    }

    @Override
    public Optional<News> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<News> findAll() {
        return null;
    }
}