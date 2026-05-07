package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.UUID;

public record CancelPendingMentoringBookingData(
    UUID userId, UserRole userRole, UUID bookingId, String cancelReason) {
  public static CancelPendingMentoringBookingData from(CancelMentoringOrderCommand command) {
    return new CancelPendingMentoringBookingData(
        command.userId(), command.userRole(), command.enrollmentId(), command.cancelReason());
  }
}
