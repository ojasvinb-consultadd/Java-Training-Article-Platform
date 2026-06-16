package com.ojasvinC.article_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;


    public SecurityConfig(
            CustomOAuth2UserService customOAuth2UserService
    ) {
        this.customOAuth2UserService = customOAuth2UserService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // Disable CSRF because we are building a REST API.
                // It can be re-enabled later if using browser forms.
                .csrf(csrf -> csrf.disable())

                // Define which endpoints require authentication.
                .authorizeHttpRequests(auth -> auth

                        // Public article endpoints
                        .requestMatchers(
                                "/",
                                "/articles",
                                "/articles/*",
                                "/oauth2/**"
                        ).permitAll()

                        // Admin endpoints require ADMIN role.
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Everything else requires login.
                        .anyRequest()
                        .authenticated()
                )

                // Configure Google OAuth login.
                .oauth2Login(oauth -> oauth

                        // After Google returns user information,
                        // use our service instead of Spring's default.
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )

                        // Temporary success page for testing.
                        .defaultSuccessUrl("/", true)
                )

                // Local application logout.
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}