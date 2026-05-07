package com.goggles.orderservice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompleteOrderPaymentCommand(
    UUID orderId, String paymentKey, Long amount, LocalDateTime approvedAt, String paymentMethod) {}
