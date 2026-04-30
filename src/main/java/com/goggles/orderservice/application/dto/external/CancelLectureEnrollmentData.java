package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.domain.model.CancelReason;
import java.util.List;
import java.util.UUID;

public record CancelLectureEnrollmentData(
    UUID userId, String userRole, List<UUID> LectureEnrollmentIds, CancelReason cancelReason) {
  public static CancelLectureEnrollmentData of(
      UUID userId, String userRole, List<UUID> LectureEnrollmentIds, CancelReason cancelReason) {
    return new CancelLectureEnrollmentData(userId, userRole, LectureEnrollmentIds, cancelReason);
  }
}
