package com.goggles.orderservice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record FailOrderPaymentCommand(
    UUID orderId, Long amount, LocalDateTime failedAt, String failureReason) {}
