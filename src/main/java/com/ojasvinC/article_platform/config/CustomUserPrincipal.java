package com.ojasvinC.article_platform.config;

import com.ojasvinC.article_platform.domain.User;
import com.ojasvinC.article_platform.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserPrincipal implements OAuth2User {

    private final User user;

    private final Map<String, Object> attributes;


    public CustomUserPrincipal(
            User user,
            Map<String, Object> attributes
    ) {
        this.user = user;
        this.attributes = attributes;
    }


    //convert UserRole enum into a Spring Security authority.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                )
        );
    }


    // Google OAuth attributes such as:
    // sub, email, name, picture
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    //unique identifier for OAuth2User.
    //using Google's "sub" field.
    @Override
    public String getName() {
        return user.getGoogleId();
    }


    public Long getId() {
        return user.getId();
    }


    public String getEmail() {
        return user.getEmail();
    }


    public UserRole getRole() {
        return user.getRole();
    }
}