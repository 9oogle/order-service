package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.domain.model.CancelReason;
import java.util.UUID;

public record CancelMentoringBookingData(
    UUID userId, UserRole userRole, UUID mentoringBookingId, CancelReason cancelReason) {
  public static CancelMentoringBookingData of(
      UUID userId, UserRole userRole, UUID mentoringBookingId, CancelReason cancelReason) {
    return new CancelMentoringBookingData(userId, userRole, mentoringBookingId, cancelReason);
  }
}
