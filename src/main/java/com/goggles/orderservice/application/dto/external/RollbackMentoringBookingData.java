package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.UUID;

public record RollbackMentoringBookingData(
    UUID userId, UUID bookingId, String cancelReason) {
  public static RollbackMentoringBookingData from(CancelMentoringOrderCommand command) {
    return new RollbackMentoringBookingData(
        command.userId(),
        command.enrollmentId(),
        command.cancelReason());
  }
}
