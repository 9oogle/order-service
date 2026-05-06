package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.util.OrderNameBuilder;
import java.util.UUID;

public record OrderPaymentPendingEvent(
    UUID orderId,
    Long amount,
    UUID customerId,
    String customerName,
    String customerEmail,
    String orderName) {
  public static OrderPaymentPendingEvent from(Order order) {
    return new OrderPaymentPendingEvent(
        order.getId(),
        order.getPrice().getFinalPrice(),
        order.getOrderer().getStudentId(),
        order.getOrderer().getStudentName(),
        order.getOrderer().getStudentEmail(),
        order.getOrderName()
    );
  }
}
