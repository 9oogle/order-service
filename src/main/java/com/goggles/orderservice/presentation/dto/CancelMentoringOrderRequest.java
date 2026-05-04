package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.UUID;

public record CancelMentoringOrderRequest(
    UUID orderId, UUID enrollmentId, String cancelReason, String cancelDescription) {
  public CancelMentoringOrderCommand toCommand(UUID userId, String userRole) {
    return new CancelMentoringOrderCommand(
        userId,
        UserRole.from(userRole),
        this.orderId,
        this.enrollmentId,
        this.cancelReason,
        this.cancelDescription);
  }
}
