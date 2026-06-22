package com.ojasvinC.article_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.AP_SOUTH_1) // Mumbai region (your AWS region)
                // uses EC2 IAM role automatically if attached
                .build();
    }
}