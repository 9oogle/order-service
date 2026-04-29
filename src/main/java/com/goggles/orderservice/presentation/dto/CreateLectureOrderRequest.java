package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateLectureOrderRequest(
    UUID couponId,
    @NotBlank(message = "결제 수단은 필수입니다.") String paymentMethod,
    @NotNull(message = "주문 상품은 필수입니다.") @Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
        List<@NotNull(message = "주문 상품 ID는 null일 수 없습니다.") UUID> items) {

  public CreateLectureOrderCommand toCommand(UUID userId, String userRole) {
    return new CreateLectureOrderCommand(
        userId, userRole, this.couponId, this.paymentMethod, this.items);
  }
}
