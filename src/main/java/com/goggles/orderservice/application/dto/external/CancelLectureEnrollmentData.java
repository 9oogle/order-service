package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.CancelReason;
import java.util.List;
import java.util.UUID;

public record CancelLectureEnrollmentData (
    UUID userId,
    List<UUID> mentoringBookingIds,
    CancelReason cancelReason
) {
  public static CancelLectureEnrollmentData of(UUID userId, List<UUID> mentoringBookingIds, CancelReason cancelReason) {
    return new CancelLectureEnrollmentData(
        userId,
        mentoringBookingIds,
        cancelReason
    );
  }
}
