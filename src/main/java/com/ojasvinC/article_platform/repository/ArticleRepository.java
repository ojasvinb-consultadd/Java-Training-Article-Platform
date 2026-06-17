package com.ojasvinC.article_platform.repository;

import com.ojasvinC.article_platform.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Find all articles created by a user
    List<Article> findByAuthorId(Long authorId);

    // Search by a single tag
    List<Article> findByTagsName(String tagName);

    @Query(
            value = "SELECT * FROM articles",
            nativeQuery = true
    )
    List<Article> findAllIncludingDeleted();


    @Query(value = """
    SELECT * 
        FROM articles
            WHERE search_vector @@ to_tsquery('english',:query)
                ORDER BY ts_rank(search_vector,to_tsquery('english',:query)) DESC
""", nativeQuery = true)
    List<Article> searchByText(@Param("query") String query);
}