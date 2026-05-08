package com.goggles.orderservice.domain.event;

import java.util.List;
import java.util.UUID;

public record LectureOrderCanceledEvent(UUID orderId, UUID userId, List<UUID> enrollmentIds) {
  public LectureOrderCanceledEvent {
    enrollmentIds = List.copyOf(enrollmentIds);
  }
}
