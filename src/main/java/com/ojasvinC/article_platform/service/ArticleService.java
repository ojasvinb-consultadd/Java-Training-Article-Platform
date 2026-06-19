package com.ojasvinC.article_platform.service;

import com.ojasvinC.article_platform.config.CustomUserPrincipal;
import com.ojasvinC.article_platform.domain.Article;
import com.ojasvinC.article_platform.domain.Tag;
import com.ojasvinC.article_platform.domain.User;
import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.dto.CreateArticleRequest;
import com.ojasvinC.article_platform.dto.UpdateArticleRequest;
import com.ojasvinC.article_platform.exception.ForbiddenException;
import com.ojasvinC.article_platform.exception.NotFoundException;
import com.ojasvinC.article_platform.repository.ArticleRepository;
import com.ojasvinC.article_platform.repository.TagRepository;
import com.ojasvinC.article_platform.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final MeterRegistry meterRegistry;

    public ArticleService(
            ArticleRepository articleRepository,
            UserRepository userRepository,
            TagRepository tagRepository,
            MeterRegistry meterRegistry
    ){
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.meterRegistry = meterRegistry;
    }

    private ArticleResponse mapToArticleResponse(Article article){
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getBody(),
                article.getAuthor().getName(),
                article.getCreatedAt(),
                article.getModifiedAt(),
                article.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
        );
    }

    private Set<Tag> resolveTags(Set<String> tags){
        Set<Tag> updatedTags = new HashSet<>();

        for(String tag : tags){
            Tag newtag = tagRepository.findByName(tag)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tag);

                        return tagRepository.save(newTag);
                    });
            updatedTags.add(newtag);
        }
        return updatedTags;
    }

    public static List<String> normalizeTags(Set<String> tags) {
        if (tags == null) return List.of(); // no tags → empty immutable list

        return tags.stream()
                .sorted() // ensures stable ordering for cache key consistency
                .toList(); // converts Set → List (ordered)
    }

    @Caching(evict = {
            @CacheEvict(value = "search", allEntries = true)
    })
    public ArticleResponse createArticle(CreateArticleRequest request, CustomUserPrincipal principal){

        Long authorId = principal.getId();

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Article article = new Article();
        article.setTitle(request.title());
        article.setBody(request.body());
        article.setAuthor(author);
        article.setTags(resolveTags(request.tags()));
        Article savedArticle = articleRepository.save(article);

        return mapToArticleResponse(savedArticle);
    }

    // cache name = "articles"
    // key = the method parameter "id"
    @Cacheable(value = "articles", key="#id")
    public ArticleResponse getArticleById(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(
                        () ->
                        {
                            return new NotFoundException("article not found");
                        });


//        Article article = articleRepository.findByIdIncludingDeleted(id)
//                .orElseThrow(() -> {
//                    log.info("Article {} does not exist", id);
//                    return new NotFoundException("Article not found");
//                });
//
//        if (article.getDeletedAt() != null) {
//            log.info("Article {} exists but is soft deleted", id);
//            throw new NotFoundException("Article not found");
//        }

        return mapToArticleResponse(article);
    }

    public List<ArticleResponse> getAllArticles() {

        return articleRepository.findAll()
                .stream()
                .map(this::mapToArticleResponse)
                .toList();
    }

    // this method is already protected as in security config we have set
    // that only users with role ADMIN can access /admin endpoints
    public List<ArticleResponse> getAllArticlesIncludingDeleted() {

        return articleRepository.findAllIncludingDeleted()
                .stream()
                .map(this::mapToArticleResponse)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(value = "articles", key = "#id"),
            @CacheEvict(value = "search", allEntries = true)
    })
    public ArticleResponse updateArticle(Long id, UpdateArticleRequest request, CustomUserPrincipal principal){


        Article article = articleRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> {
                    log.info("Article {} does not exist", id);
                    return new NotFoundException("Article not found");
                });

        if (article.getDeletedAt() != null) {
            log.info("Article {} exists but is soft deleted", id);
            throw new NotFoundException("Article not found");
        }

        Long currentUserId = principal.getId();

        boolean isOwner = article.getAuthor().getId().equals(currentUserId);
//        boolean isAdmin = principal.getRole().name().equals("ADMIN");

        if (!isOwner) { //add && !isAdmin to allow admin to edit articles
            throw new ForbiddenException("Not authorized to update this article");
        }

        if (request.title() != null){
            article.setTitle(request.title());
        }

        if (request.body() != null) {
            article.setBody(request.body());
        }

        if (request.tags() != null){
            Set<Tag> updatedTags = resolveTags(request.tags());
            article.setTags(updatedTags);
        }

        Article updatedArticle = articleRepository.save(article);

        return mapToArticleResponse(updatedArticle);
    }

    @Caching(evict = {
            @CacheEvict(value = "articles", key = "#id"),
            @CacheEvict(value = "search", allEntries = true)
    })
    public void deleteArticle(Long id, CustomUserPrincipal principal){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not Found"));

        Long currentUserId = principal.getId();

        boolean isOwner = article.getAuthor().getId().equals(currentUserId);
        boolean isAdmin = principal.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Not authorized to delete this article");
        }

        article.setDeletedAt(LocalDateTime.now());
        articleRepository.save(article);
    }

    @Cacheable(
            value = "search",  // separate cache region
            key = """
                    #q + '-' +
                        T(com.ojasvinC.article_platform.service.ArticleService)
                            .normalizeTags(#tags == null ? new java.util.HashSet() : #tags).toString()
                    """
    )
    public List<ArticleResponse> searchArticles(String q, Set<String> tags){
        boolean hasQuery = q != null && !q.trim().isEmpty();
        boolean hasTags = tags !=null && !tags.isEmpty();

        Timer searchTimer = Timer.builder("search.query.latency")
                .description("Tracks the database execution latency for hybrid search")
                .tag("has_text", String.valueOf(hasQuery))
                .tag("has_tags", String.valueOf(hasTags))
                .register(meterRegistry);
        return searchTimer.record(() ->{
            if (!hasTags && !hasQuery) {
                return articleRepository.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::mapToArticleResponse)
                        .toList();
            }

            String ts_query = null;

            if(hasQuery){
                ts_query = String.join(" & ",q.trim().split("\\s+"));
            }

            List<String> tagList = hasTags ? new ArrayList<>(tags) : null;

            return articleRepository
                    .searchHybrid(
                            ts_query,
                            tagList,
                            hasTags ? tags.size() : 0
                    )
                    .stream()
                    .map(this::mapToArticleResponse)
                    .toList();
        });
    }


}