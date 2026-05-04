package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.UUID;

public record CancelMentoringBookingData(
    UUID userId,
    UserRole userRole,
    UUID orderId,
    UUID bookingId,
    String cancelReason,
    String cancelDescription) {
  public static CancelMentoringBookingData from(CancelMentoringOrderCommand command) {
    return new CancelMentoringBookingData(
        command.userId(),
        command.userRole(),
        command.orderId(),
        command.enrollmentId(),
        command.cancelReason(),
        command.cancelDescription());
  }
}
