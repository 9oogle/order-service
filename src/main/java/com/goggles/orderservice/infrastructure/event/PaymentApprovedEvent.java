package com.goggles.orderservice.infrastructure.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentApprovedEvent(
    UUID orderId, String paymentKey, Long amount, LocalDateTime approvedAt, String paymentMethod) {}
