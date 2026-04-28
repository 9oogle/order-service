package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.presentation.dto.CreateLectureOrderRequest;
import java.util.List;
import java.util.UUID;

public record CreateLectureOrderCommand(
    UUID userId, String userRole, UUID couponId, String paymentMethod, List<UUID> items) {

  public static CreateLectureOrderCommand from(
      CreateLectureOrderRequest request, UUID userId, String userRole) {
    return new CreateLectureOrderCommand(
        userId, userRole, request.couponId(), request.paymentMethod(), request.items());
  }
}
