package com.ojasvinC.article_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Custom service that:
    // 1. Calls Google (via Spring default OAuth service)
    // 2. Extracts user info
    // 3. Creates / fetches user from DB
    // 4. Returns CustomUserPrincipal
    private final CustomOAuth2UserService customOAuth2UserService;

    // Holds Google client registration details from application.yml
    // (client-id, secret, scopes, redirect-uri, etc.)
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(
            CustomOAuth2UserService customOAuth2UserService,
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // CSRF protects browser form submissions using session cookies.
                // Disabled because:
                // - We are building REST APIs
                // - We are not using server-rendered forms
                // - Testing via Postman/frontend
                .csrf(csrf -> csrf.disable())

                // Authorization rules for endpoints
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/",
                                "/articles",
                                "/articles/*",
                                "/oauth2/**",   // OAuth flow endpoints
                                "/logged-out"
                        ).permitAll()

                        // Only ADMIN role can access admin APIs
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Everything else requires login
                        .anyRequest()
                        .authenticated()
                )

                // OAuth2 login configuration (Google login flow)
                .oauth2Login(oauth -> oauth

                        // Controls the initial redirect to Google
                        .authorizationEndpoint(auth ->
                                auth.authorizationRequestResolver(
                                        authorizationRequestResolver(clientRegistrationRepository)
                                )
                        )

                        // After Google returns user info:
                        // replaces Spring default user handling
                        // with DB-backed user creation/login logic
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )

                        // After successful login, always redirect to "/"
                        .defaultSuccessUrl("/", true)
                )

                // Logout configuration for local session only
                .logout(logout -> logout

                        // Spring logout endpoint
                        .logoutUrl("/logout")

                        // Invalidate HTTP session (removes JSESSIONID server-side)
                        .invalidateHttpSession(true)

                        // Clears Authentication object from SecurityContext
                        .clearAuthentication(true)

                        // Removes session cookie from browser
                        .deleteCookies("JSESSIONID")

                        // Redirect after logout
                        .logoutSuccessUrl("/logged-out")
                );

        return http.build();
    }

    // Customizes OAuth2 authorization request BEFORE redirecting to Google
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository repo
    ) {

        // Default resolver builds OAuth redirect URLs like:
        // /oauth2/authorization/google
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        repo,
                        "/oauth2/authorization"
                );

        // Forces Google account chooser screen every login
        // prevents automatic reuse of previous Google session
        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> {
                    params.put("prompt", "select_account");
                })
        );

        return resolver;
    }
}