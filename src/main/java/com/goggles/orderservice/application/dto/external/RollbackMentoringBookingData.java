package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import java.util.UUID;

public record RollbackMentoringBookingData(
    UUID userId, UserRole userRole, UUID bookingId, String cancelReason) {}
