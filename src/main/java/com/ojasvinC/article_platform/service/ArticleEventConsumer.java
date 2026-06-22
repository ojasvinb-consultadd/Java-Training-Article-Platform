package com.ojasvinC.article_platform.service;

import com.ojasvinC.article_platform.events.ArticleEvent;
import com.ojasvinC.article_platform.repository.ArticleViewCountRepository;
import com.ojasvinC.article_platform.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;

@Component
public class ArticleEventConsumer {

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    private final ArticleViewCountRepository viewCountRepository;
    private final SqsClient sqsClient;

    public ArticleEventConsumer(
            ArticleViewCountRepository viewCountRepository,
            SqsClient sqsClient)
    {
        this.viewCountRepository = viewCountRepository;
        this.sqsClient = sqsClient;
    }

    // In real AWS this is triggered by polling or Spring Cloud AWS listener
    public void processMessage(String message) {

        ArticleEvent event = JsonUtil.fromJson(message, ArticleEvent.class);

        // ONLY VIEW tracking
        if (!"VIEW".equals(event.getType())) {
            return; // ignore anything else
        }

        updateViewCount(event.getArticleId());
    }

    private void updateViewCount(Long articleId) {
        viewCountRepository.increment(articleId);
    }

    @Scheduled(fixedDelay = 5000)
    public void pollSqs() {

        System.out.println("Polling SQS..."); // DEBUG check if scheduler runs

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(2)
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();

        System.out.println("Messages received: " + messages.size()); // DEBUG

        for (Message message : messages) {
            System.out.println("Message: " + message.body()); // DEBUG

            processMessage(message.body());

            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());
        }
    }
}