package com.goggles.orderservice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelOrderPaymentCommand(
    UUID orderId, Long amount, LocalDateTime cancelAt, String cancelReason) {}
