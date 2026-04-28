package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.presentation.dto.CreateMentoringOrderRequest;
import com.goggles.orderservice.presentation.dto.CreateMentoringOrderRequest.MentoringTimeSlots;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateMentoringOrderCommand(
    UUID userId,
    String userRole,
    UUID mentoringId,
    String requestMessage,
    UUID couponId,
    String paymentMethod,
    List<TimeSlot> timeSlots) {
  public record TimeSlot(LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
    public static TimeSlot from(MentoringTimeSlots timeSlots) {
      return new TimeSlot(timeSlots.date(), timeSlots.startTime(), timeSlots.endTime());
    }
  }

  public static CreateMentoringOrderCommand of(
      CreateMentoringOrderRequest request, UUID userId, String userRole) {
    return new CreateMentoringOrderCommand(
        userId,
        userRole,
        request.mentoringId(),
        request.requestMessage(),
        request.couponId(),
        request.paymentMethod(),
        request.items().stream().map(TimeSlot::from).toList());
  }
}
