package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.RollbackLectureEnrollmentData;
import java.util.List;
import java.util.UUID;

public record RollbackLectureEnrollmentRequest(List<UUID> enrollmentIds, String cancelReason) {
  public static RollbackLectureEnrollmentRequest from(RollbackLectureEnrollmentData data) {
    return new RollbackLectureEnrollmentRequest(data.enrollmentIds(), data.cancelReason());
  }
}
