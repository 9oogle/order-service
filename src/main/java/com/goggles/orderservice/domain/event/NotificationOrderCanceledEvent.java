package com.goggles.orderservice.domain.event;

import com.goggles.common.util.TimeUtil;
import com.goggles.orderservice.domain.model.Order;
import java.time.Instant;
import java.util.UUID;

public record NotificationOrderCanceledEvent(
    UUID orderId,
    String customerEmail,
    String customerName,
    String orderName,
    Long amount,
    Instant cancelledAt,
    String cancelReason) {
  public static NotificationOrderCanceledEvent from(Order order) {
    return new NotificationOrderCanceledEvent(
        order.getId(),
        order.getOrderer().getStudentEmail(),
        order.getOrderer().getStudentName(),
        order.getOrderName(),
        order.getPrice().getFinalPrice(),
        TimeUtil.toInstant(order.getCanceledAt()),
        order.getCancelReason().name());
  }
}
