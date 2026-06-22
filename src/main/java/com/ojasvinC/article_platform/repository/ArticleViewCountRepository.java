package com.ojasvinC.article_platform.repository;

import com.ojasvinC.article_platform.domain.ArticleViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ArticleViewCountRepository extends JpaRepository<ArticleViewCount, Long> {

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO article_view_counts (article_id, view_count, updated_at)
        VALUES (:articleId, 1, now())
        ON CONFLICT (article_id)
        DO UPDATE SET
            view_count = article_view_counts.view_count + 1,
            updated_at = now()
    """, nativeQuery = true)
    void increment(@Param("articleId") Long articleId);
}