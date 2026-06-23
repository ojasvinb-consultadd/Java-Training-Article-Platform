package com.ojasvinC.article_platform.repository;

import com.ojasvinC.article_platform.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query(
            value = """
                SELECT *
                FROM articles
                WHERE id = :id
                """,
            nativeQuery = true
    )
    Optional<Article> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = """
        SELECT * FROM articles
        WHERE deleted_at IS NOT NULL
        """, nativeQuery = true)
    List<Article> findAllSoftDeleted();


    @Query(value = """
            SELECT a.*
            FROM articles a
            WHERE
            (
                :query IS NULL OR a.search_vector @@ to_tsquery('english', :query)
            )
            AND
            (
                :tagCount = 0 OR EXISTS (
                    SELECT 1
                    FROM article_tags at
                    JOIN tags t ON t.id = at.tag_id
                    WHERE at.article_id = a.id
                    AND (:tagCount = 0 OR t.name IN (:tags))
                )
            )
            ORDER BY
            CASE
                WHEN :query IS NULL THEN EXTRACT(EPOCH FROM a.created_at)
                ELSE ts_rank(a.search_vector, to_tsquery('english', :query))
            END DESC
    """, nativeQuery = true)
    List<Article> searchHybrid(
            @Param("query") String query,
            @Param("tags") List<String> tags,
            @Param("tagCount") int tagCount
    );

    List<Article> findAllByOrderByCreatedAtDesc();
}