package com.goggles.orderservice.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentConfirmedEvent(
    UUID orderId, String paymentKey, Long amount, Instant approvedAt, String paymentMethod) {}
