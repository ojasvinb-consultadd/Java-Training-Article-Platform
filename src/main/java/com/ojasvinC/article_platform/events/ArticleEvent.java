package com.ojasvinC.article_platform.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEvent {

    private Long articleId;  // article being viewed
    private Long userId;     // optional (can be null)
    private String type;     // VIEW only (future-proofing)
}