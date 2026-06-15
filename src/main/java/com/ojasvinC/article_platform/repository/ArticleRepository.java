package com.ojasvinC.article_platform.repository;

import com.ojasvinC.article_platform.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Find all articles created by a user
    List<Article> findByAuthorId(Long authorId);

    // Search by a single tag
    List<Article> findByTagsName(String tagName);
}