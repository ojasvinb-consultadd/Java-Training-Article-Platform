package com.ojasvinC.article_platform.api;

import com.ojasvinC.article_platform.dto.ArticleResponse;
import com.ojasvinC.article_platform.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {
    private final ArticleService articleService;

    public AdminArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping
    public List<ArticleResponse> getAllArticlesIncludingDeleted() {
        return articleService.getAllArticlesIncludingDeleted();
    }
}
