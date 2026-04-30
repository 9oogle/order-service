package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.application.common.UserRole;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateMentoringOrderCommand(
    UUID userId,
    UserRole userRole,
    UUID mentoringId,
    String requestMessage,
    UUID couponId,
    String paymentMethod,
    List<TimeSlot> timeSlots) {
  public record TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {}
}
