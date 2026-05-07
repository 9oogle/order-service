package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationOrderCompletedEvent(
    UUID orderId,
    String customerEmail,
    String customerName,
    String orderName,
    Long amount,
    LocalDateTime approvedAt) {
  public static NotificationOrderCompletedEvent of(Order order, LocalDateTime approvedAt) {
    return new NotificationOrderCompletedEvent(
        order.getId(),
        order.getOrderer().getStudentEmail(),
        order.getOrderer().getStudentName(),
        order.getOrderName(),
        order.getPrice().getFinalPrice(),
        approvedAt);
  }
}
