package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.application.common.UserRole;
import java.util.UUID;

public record CancelMentoringOrderCommand(
    UUID userId,
    UserRole userRole,
    UUID orderId,
    UUID enrollmentId,
    String cancelReason,
    String cancelDescription) {}
