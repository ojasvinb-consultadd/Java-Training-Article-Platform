package com.ojasvinC.article_platform.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_view_counts")
@Getter
@Setter
@NoArgsConstructor
public class ArticleViewCount {

    @Id
    @Column(name = "article_id")
    private Long articleId;
    // primary key = article id (1 row per article)

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    // stores total number of views

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // last time view count was updated
}