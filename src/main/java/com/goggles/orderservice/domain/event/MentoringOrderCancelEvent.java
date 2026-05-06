package com.goggles.orderservice.domain.event;

import java.util.UUID;

public record MentoringOrderCancelEvent(
    UUID orderId, UUID userId, UUID enrollmentId, String cancelReason, String cancelDescription
) {}
