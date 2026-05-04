package com.goggles.orderservice.domain.event;

import java.util.UUID;

public record MentoringOrderCompletionEvent(UUID orderId, UUID userId, UUID enrollmentId) {

}
