package com.goggles.orderservice.infrastructure.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics.order")
public record OrderTopics(String paymentPending, String paymentCancel,
                          String notificationCompleted, String notificationCancelled,
                          String lectureCompleted, String mentoringCompleted,
                          String lectureCancelled, String mentoringCancelled) {}
