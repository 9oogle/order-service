package com.goggles.orderservice.application.dto.command;

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
  public record TimeSlot(LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {}
}
