package com.goggles.orderservice.domain.event;

import com.goggles.common.util.TimeUtil;
import com.goggles.orderservice.domain.model.Order;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationOrderCompletedEvent(
    UUID orderId,
    UUID customerId,
    String customerEmail,
    String customerName,
    String orderName,
    Long amount,
    Instant approvedAt) {
  public static NotificationOrderCompletedEvent of(Order order, LocalDateTime approvedAt) {
    return new NotificationOrderCompletedEvent(
        order.getId(),
        order.getOrderer().getStudentId(),
        order.getOrderer().getStudentEmail(),
        order.getOrderer().getStudentName(),
        order.getOrderName(),
        order.getPrice().getFinalPrice(),
        TimeUtil.toInstant(approvedAt));
  }
}
