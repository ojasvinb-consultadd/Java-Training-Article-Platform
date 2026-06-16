package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.config.CustomUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Login successful";
    }

    @GetMapping("/me")
    public Map<String, Object> me(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }
}