package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.application.common.UserRole;
import java.util.List;
import java.util.UUID;

public record CreateLectureOrderCommand(
    UUID userId, UserRole userRole, UUID couponId, String paymentMethod, List<UUID> items) {}
