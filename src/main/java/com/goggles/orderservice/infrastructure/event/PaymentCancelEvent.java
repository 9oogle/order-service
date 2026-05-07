package com.goggles.orderservice.infrastructure.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCancelEvent(
    UUID orderId, Long amount, LocalDateTime cancelAt, String cancelReason) {}
