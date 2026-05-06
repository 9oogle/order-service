package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.application.common.UserRole;
import java.util.List;
import java.util.UUID;

public record CancelLectureOrderCommand(
    UUID userId,
    UserRole userRole,
    UUID orderId,
    List<UUID> enrollmentIds,
    String cancelReason,
    String cancelDescription) {}
