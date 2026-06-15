package com.ojasvinC.article_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateArticleRequest(
        @Size(max = 255)
        String title,

        @NotBlank
        String body,

        Set<String> tags
) {
}