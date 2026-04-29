package com.goggles.orderservice.application.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateLectureOrderCommand(
    UUID userId, String userRole, UUID couponId, String paymentMethod, List<UUID> items) {}
