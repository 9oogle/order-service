package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.UUID;

public record CancelPendingMentoringBookingData(UUID userId, UUID bookingId, String cancelReason) {
  public static CancelPendingMentoringBookingData from(CancelMentoringOrderCommand command) {
    return new CancelPendingMentoringBookingData(
        command.userId(), command.enrollmentId(), command.cancelReason());
  }
}
