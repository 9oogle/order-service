package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CancelLectureOrderRequest(
    @NotNull(message = "주문 ID는 필수입니다.") UUID orderId,
    @NotNull(message = "등록 ID는 필수입니다.") @Size(min = 1, message = "등록 ID는 최소 1개 이상이어야 합니다.")
        List<@NotNull(message = "등록 ID 값은 null일 수 없습니다.") UUID> enrollmentIds,
    @NotBlank(message = "취소 이유는 필수입니다.") String cancelReason,
    String cancelDescription) {
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
