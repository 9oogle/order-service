package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationOrderCanceledEvent(
    UUID orderId, String customerEmail, String customerName, String orderName, Long amount, LocalDateTime cancelledAt, String cancelReason
) {
  public static NotificationOrderCanceledEvent from(Order order) {
    return new NotificationOrderCanceledEvent(
        order.getId(),
        order.getOrderer().getStudentEmail(),
        order.getOrderer().getStudentName(),
        order.getOrderName(),
        order.getPrice().getFinalPrice(),
        order.getCanceledAt(),
        order.getCancelReason().name()
    );
  }
}
