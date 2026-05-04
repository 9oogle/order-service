package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import java.util.List;
import java.util.UUID;

public record CancelLectureEnrollmentData(
    UUID userId,
    UserRole userRole,
    UUID orderId,
    List<UUID> enrollmentIds,
    String cancelReason,
    String cancelDescription) {
  public static CancelLectureEnrollmentData from(CancelLectureOrderCommand command) {
    return new CancelLectureEnrollmentData(
        command.userId(),
        command.userRole(),
        command.orderId(),
        command.enrollmentIds(),
        command.cancelReason(),
        command.cancelDescription());
  }
}
