package com.goggles.orderservice.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentCancelEvent(
    UUID orderId, Long amount, Instant cancelAt, String cancelReason) {}
