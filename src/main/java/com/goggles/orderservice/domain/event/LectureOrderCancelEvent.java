package com.goggles.orderservice.domain.event;

import java.util.List;
import java.util.UUID;

public record LectureOrderCancelEvent(
    UUID orderId, UUID userId, List<UUID> enrollmentIds
) {}
