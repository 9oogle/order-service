package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import java.util.List;
import java.util.UUID;

public record CancelLectureOrderRequest(
    UUID orderId, List<UUID> enrollmentIds, String cancelReason, String cancelDescription) {
  public CancelLectureOrderCommand toCommand(UUID userId, String userRole) {
    return new CancelLectureOrderCommand(
        userId,
        UserRole.from(userRole),
        this.orderId,
        this.enrollmentIds,
        this.cancelReason,
        this.cancelDescription);
  }
}
