package com.ojasvinC.article_platform.service;

import com.ojasvinC.article_platform.domain.Article;
import com.ojasvinC.article_platform.domain.Tag;
import com.ojasvinC.article_platform.domain.User;
import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.dto.CreateArticleRequest;
import com.ojasvinC.article_platform.dto.UpdateArticleRequest;
import com.ojasvinC.article_platform.exception.NotFoundException;
import com.ojasvinC.article_platform.repository.ArticleRepository;
import com.ojasvinC.article_platform.repository.TagRepository;
import com.ojasvinC.article_platform.repository.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public ArticleService(
            ArticleRepository articleRepository,
            UserRepository userRepository,
            TagRepository tagRepository
    ){
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
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

    public ArticleResponse createArticle(CreateArticleRequest request){

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<Tag> tags = new HashSet<>();

        for (String tagName : request.tags()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);

                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        Article article = new Article();
        article.setTitle(request.title());
        article.setBody(request.body());
        article.setAuthor(author);
        article.setTags(tags);
        Article savedArticle = articleRepository.save(article);

        return mapToArticleResponse(savedArticle);
    }


    public ArticleResponse getArticleById(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> {
                    return new NotFoundException("Article not Found");
                });
        return mapToArticleResponse(article);
    }

    public List<ArticleResponse> getAllArticles() {

        return articleRepository.findAll()
                .stream()
                .map(this::mapToArticleResponse)
                .toList();
    }

    public List<ArticleResponse> getAllArticlesIncludingDeleted() {
        return articleRepository.findAllIncludingDeleted()
                .stream()
                .map(this::mapToArticleResponse)
                .toList();
    }

    public ArticleResponse updateArticle(Long id, UpdateArticleRequest request){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> {
                    return new NotFoundException("Article not Found");
                });

        if (request.title() != null){
            article.setTitle(request.title());
        }

        if (request.body() != null) {
            article.setBody(request.body());
        }

        if (request.tags() != null){
            Set<Tag> updatedTags = new HashSet<>();

            for(String tag : request.tags()){
                Tag newtag = tagRepository.findByName(tag)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tag);

                            return tagRepository.save(newTag);
                        });
                updatedTags.add(newtag);
            }
            article.setTags(updatedTags);
        }

        Article updatedArticle = articleRepository.save(article);

        return mapToArticleResponse(updatedArticle);
    }


    public void deleteArticle(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(()-> {
                    return new NotFoundException("Article not Found");
                });

        article.setTitle("Deleted : " +article.getTitle());

        article.setDeletedAt(LocalDateTime.now());

        articleRepository.save(article);
    }



}