package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.CancelReason;
import java.util.UUID;

public record CancelMentoringBookingData(
    UUID userId,
    UUID mentoringBookingId,
    CancelReason cancelReason
) {
  public static CancelMentoringBookingData of(UUID userId, UUID mentoringBookingId, CancelReason cancelReason) {
    return new CancelMentoringBookingData(
        userId,
        mentoringBookingId,
        cancelReason
    );
  }
}
