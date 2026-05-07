package com.goggles.orderservice.domain.event;

import java.util.UUID;

public record MentoringOrderCanceledEvent(UUID orderId, UUID userId, UUID enrollmentId) {}
