package com.goggles.orderservice.infrastructure.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFailedEvent(
    UUID orderId, Long amount, Instant failedAt, String failureReason) {}
