package com.goggles.orderservice.domain.event;

import java.util.List;
import java.util.UUID;

public record LectureOrderCompletionEvent(UUID orderId, UUID userId, List<UUID> enrollmentIds) {
}