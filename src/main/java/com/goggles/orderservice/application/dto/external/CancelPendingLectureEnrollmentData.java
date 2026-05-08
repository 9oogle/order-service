package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import java.util.List;
import java.util.UUID;

public record CancelPendingLectureEnrollmentData(
    UUID userId, UserRole userRole, List<UUID> enrollmentIds, String cancelReason) {
  public static CancelPendingLectureEnrollmentData from(CancelLectureOrderCommand command) {
    return new CancelPendingLectureEnrollmentData(
        command.userId(), command.userRole(), command.enrollmentIds(), command.cancelReason());
  }
}
