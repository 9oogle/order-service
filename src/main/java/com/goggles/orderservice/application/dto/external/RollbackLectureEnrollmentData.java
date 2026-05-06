package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import java.util.List;
import java.util.UUID;

public record RollbackLectureEnrollmentData(
    UUID userId, UserRole userRole, List<UUID> enrollmentIds, String cancelReason) {}
