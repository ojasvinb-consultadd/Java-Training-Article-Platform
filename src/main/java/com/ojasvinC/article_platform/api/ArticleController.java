package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.config.CustomUserPrincipal;
import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.dto.CreateArticleRequest;
import com.ojasvinC.article_platform.dto.UpdateArticleRequest;
import com.ojasvinC.article_platform.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponse createArticle(
            @Valid @RequestBody CreateArticleRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
            ) {
        return articleService.createArticle(request, principal);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse getArticleById(@PathVariable Long id){
        return articleService.getArticleById(id);
    }

    @GetMapping
    public List<ArticleResponse> getAllArticles() {
        return articleService.getAllArticles();
    }

    @PatchMapping("/{id}")
    public ArticleResponse updateArticle(
            @PathVariable Long id,
            @RequestBody UpdateArticleRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return articleService.updateArticle(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //204 means successful but no response body
    public void deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        articleService.deleteArticle(id, principal);
    }

    @GetMapping("/search")
    public List<ArticleResponse> searchArticles(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Set<String> tags
    ){
        return articleService.searchArticles(q,tags);
    }


}