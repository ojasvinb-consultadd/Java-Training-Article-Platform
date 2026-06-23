package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.config.CustomUserPrincipal;
import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {
    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping
    public List<ArticleResponse> getAllArticlesIncludingDeleted(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return articleService.getAllArticlesIncludingDeleted(principal);
    }

    @GetMapping("/deleted")
    public List<ArticleResponse> getDeletedArticles(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return articleService.getDeletedArticles(principal);
    }

    @DeleteMapping("/{id}/hard-delete")
    public ResponseEntity<Void> hardDeleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        articleService.hardDeleteArticle(id, principal);

        return ResponseEntity.noContent().build();
    }
}
