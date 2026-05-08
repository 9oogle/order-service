package com.goggles.orderservice.infrastructure.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentApprovedEvent(
    UUID orderId, String paymentKey, Long amount, Instant approvedAt, String paymentMethod) {}
