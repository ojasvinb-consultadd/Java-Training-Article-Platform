package com.ojasvinC.article_platform.service;

import com.ojasvinC.article_platform.events.ArticleEvent;
import com.ojasvinC.article_platform.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class SqsPublisher {

    private final SqsClient sqsClient;


    @Value("${aws.sqs.queue-url:dummyValue}")
    private String queueUrl;

    public SqsPublisher(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void publishViewEvent(ArticleEvent event) {

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(JsonUtil.toJson(event))
                .build());
    }
}