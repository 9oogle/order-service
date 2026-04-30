package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateMentoringOrderRequest(
    UUID couponId,
    @NotNull(message = "멘토링 ID는 필수입니다.") UUID mentoringId,
    String requestMessage,
    @NotBlank(message = "결제 수단은 필수입니다.") String paymentMethod,
    @NotNull(message = "주문 상품은 필수입니다.") @Size(min = 1, message = "주문 상품은 최소 1개 이상이어야 합니다.")
        List<MentoringTimeSlots> items) {

  public record MentoringTimeSlots(
      @NotNull(message = "멘토링 날짜는 필수입니다.") LocalDate date,
      @NotNull(message = "멘토링 시작 시간은 필수입니다.") LocalTime startTime,
      @NotNull(message = "멘토링 종료 시간은 필수입니다.") LocalTime endTime) {}

  public CreateMentoringOrderCommand toCommand(UUID userId, String userRole) {
    return new CreateMentoringOrderCommand(
        userId,
        UserRole.from(userRole),
        this.mentoringId,
        this.requestMessage,
        this.couponId,
        this.paymentMethod,
        this.items.stream()
            .map(
                item ->
                    new CreateMentoringOrderCommand.TimeSlot(
                        item.date(), item.startTime(), item.endTime()))
            .toList());
  }
}
