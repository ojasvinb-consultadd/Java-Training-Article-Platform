package com.ojasvinC.article_platform.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "articles")
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "body", nullable = false, length = Integer.MAX_VALUE)
    private String body;

    //here as a single user can have many articles we define
    //the relationship here and tell hibernate this Article is owned by
    //single user only and then tell it where the user is located

    //don't load the data from the relationship when loading user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    // here the article tag table acts as the middleman showing the
    // relationship between the tags and the articles tables
    // so instead of just using joinColumns we also have to use inverseJoin to
    // show the relationship basically making the article tag table as a NAT or network bridge
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_tags",
            // the column in article_tags pointing to the article table
            joinColumns = @JoinColumn(name = "article_id"),
            // the column in article_tags pointing to the tag table
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    //even tho we are connecting to the article_tags table
    //we set the type as tags and again use lazy load to not
    //load data unless explicitly called
    private Set<Tag> tags = new HashSet<>();
}
