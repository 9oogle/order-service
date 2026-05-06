package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CancelMentoringOrderRequest(
    @NotNull(message = "주문 ID는 필수입니다.") UUID orderId,
    @NotNull(message = "예약 ID는 필수입니다.") UUID enrollmentId,
    @NotBlank(message = "취소 이유는 필수입니다.") String cancelReason,
    String cancelDescription) {
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
