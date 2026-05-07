package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import java.util.List;
import java.util.UUID;

public record RollbackLectureEnrollmentData(
    UUID userId, List<UUID> enrollmentIds, String cancelReason) {
  public static RollbackLectureEnrollmentData from(CancelLectureOrderCommand command) {
    return new RollbackLectureEnrollmentData(
        command.userId(),
        command.enrollmentIds(),
        command.cancelReason());
  }
}
