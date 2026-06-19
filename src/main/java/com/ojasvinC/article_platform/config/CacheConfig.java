package com.ojasvinC.article_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. Define a default configuration (2 hours fallback)
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .disableCachingNullValues();

        // 2. Map your actual @Cacheable values to your custom TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("search",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));

        cacheConfigurations.put("articles",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));

        cacheConfigurations.put("allArticles",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));

        // 3. Build and return the cache manager
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}