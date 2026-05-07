package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import java.util.UUID;

public record OrderPaymentCancelEvent(
    UUID orderId,
    Long amount,
    UUID customerId,
    String customerName,
    String customerEmail,
    String orderName,
    String cancelReason,
    String cancelDescription) {
  public static OrderPaymentCancelEvent from(Order order) {
    return new OrderPaymentCancelEvent(
        order.getId(),
        order.getPrice().getFinalPrice(),
        order.getOrderer().getStudentId(),
        order.getOrderer().getStudentName(),
        order.getOrderer().getStudentEmail(),
        order.getOrderName(),
        order.getCancelReason().name(),
        order.getCancelDescription());
  }
}
