package com.ojasvinC.article_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching //makes spring boot look for annotations like @Cacheable, @CacheEvict, @CachePut
@SpringBootApplication
@EnableScheduling
@Slf4j //sets up logging
public class ArticlePlatformApplication {
	public static void main(String[] args) {
		log.info("Application Starting");
		SpringApplication.run(ArticlePlatformApplication.class, args);
	}

}
