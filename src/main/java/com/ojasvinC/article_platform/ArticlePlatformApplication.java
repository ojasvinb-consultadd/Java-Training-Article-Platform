package com.ojasvinC.article_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ArticlePlatformApplication {
	public static void main(String[] args) {
		log.info("Application Starting");
		SpringApplication.run(ArticlePlatformApplication.class, args);
	}

}
