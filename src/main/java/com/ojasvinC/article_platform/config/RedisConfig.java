package com.ojasvinC.article_platform.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// JACKSON 3 IMPORTS
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 1. Build the secure type validator required for Jackson 3
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        // 2. Leverage Spring Data Redis 4.0's idiomatic functional creator API.
        // This automatically configures an underlying immutable JsonMapper instance.
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.create(builder -> builder
                .typePropertyName("@class")
                .enableDefaultTyping(typeValidator)
                .enableSpringCacheNullValueSupport("@class")
        );

        // 3. Define global cache behavior
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // 4. Return the configured manager
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration("search", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("articles", defaultCacheConfig.entryTtl(Duration.ofHours(1)))
                .build();
    }
}