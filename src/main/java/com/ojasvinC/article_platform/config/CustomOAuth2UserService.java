package com.ojasvinC.article_platform.config;

import com.ojasvinC.article_platform.domain.User;
import com.ojasvinC.article_platform.domain.UserRole;
import com.ojasvinC.article_platform.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    public CustomOAuth2UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    @Override
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {

        // Calls Google and retrieves the user information
        OAuth2User oauthUser = super.loadUser(userRequest);

        // Google returns all fields in a map
        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");


        // Check if this Google account already exists
        User user = userRepository
                .findByGoogleId(googleId)
                .orElseGet(() -> {

                    // First login, create a new user
                    User newUser = new User();

                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole(UserRole.USER);
                    newUser.setCreatedAt(LocalDateTime.now());

                    return userRepository.save(newUser);
                });


        // Store our custom principal in Spring Security
        return new CustomUserPrincipal(
                user,
                oauthUser.getAttributes()
        );
    }
}