package com.ojasvinC.article_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateArticleRequest(
        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        String body,

        Long authorId,

        Set<String> tags
) {
}