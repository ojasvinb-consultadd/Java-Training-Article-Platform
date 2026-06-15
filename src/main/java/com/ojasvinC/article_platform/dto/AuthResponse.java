package com.ojasvinC.article_platform.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}