package com.ojasvinC.article_platform.repository;

import com.ojasvinC.article_platform.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // Used when creating articles and reusing existing tags
    Optional<Tag> findByName(String name);
}