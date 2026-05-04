package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelLectureEnrollmentData;
import java.util.List;
import java.util.UUID;

public record CancelLectureEnrollmentRequest(
    UUID orderId, List<UUID> enrollmentIds, String cancelReason, String cancelDescription) {
  public static CancelLectureEnrollmentRequest from(CancelLectureEnrollmentData data) {
    return new CancelLectureEnrollmentRequest(
        data.orderId(), data.enrollmentIds(), data.cancelReason(), data.cancelDescription());
  }
}
