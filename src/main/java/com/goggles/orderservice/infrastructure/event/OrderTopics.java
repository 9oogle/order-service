package com.goggles.orderservice.infrastructure.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics.order")
public record OrderTopics(String paymentPending, String cancelled) {}
