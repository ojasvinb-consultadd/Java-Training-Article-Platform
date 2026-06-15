package com.ojasvinC.article_platform.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleResponse(
        Long id,
        String title,
        String body,
        String author,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Set<String> tags
) {
}