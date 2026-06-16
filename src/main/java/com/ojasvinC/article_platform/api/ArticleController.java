package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.dto.CreateArticleRequest;
import com.ojasvinC.article_platform.dto.UpdateArticleRequest;
import com.ojasvinC.article_platform.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @Valid @RequestBody CreateArticleRequest request
            ) {
        return articleService.createArticle(request);
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
            @RequestBody UpdateArticleRequest request
    ) {
        return articleService.updateArticle(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 means successful but no response body
    public void deleteArticle(
            @PathVariable Long id
    ) {
        articleService.deleteArticle(id);
    }




}