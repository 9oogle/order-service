package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelPendingLectureEnrollmentData;
import java.util.List;
import java.util.UUID;

public record CancelPendingLectureEnrollmentRequest(List<UUID> enrollmentIds, String cancelReason) {
  public static CancelPendingLectureEnrollmentRequest from(
      CancelPendingLectureEnrollmentData data) {
    return new CancelPendingLectureEnrollmentRequest(data.enrollmentIds(), data.cancelReason());
  }
}
