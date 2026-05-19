package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import java.time.Instant;
import java.util.UUID;

public record OrderFailedEvent(
    UUID orderId, UUID customerId, String customerEmail, String failureReason, Instant failedAt) {
  public static OrderFailedEvent of(Order order, String failureReason) {
    return new OrderFailedEvent(
        order.getId(),
        order.getOrderer().getStudentId(),
        order.getOrderer().getStudentEmail(),
        failureReason,
        Instant.now());
  }
}
