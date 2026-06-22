package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.config.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {
    @Value("${spring.application.version}")
    private String version;

    @GetMapping("/")
    public String home() {
        return "home page";
    }

    @GetMapping("/login-complete")
    public String loginComplete() {
        return "Logged in successfully";
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        // This endpoint is hit ONLY after /logout
        // You can now decide what to do next
        return "Logged out successfully. Please login again.";
    }

    @GetMapping("/me")
    public Map<String, Object> me(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "Version", version

        );
    }
}