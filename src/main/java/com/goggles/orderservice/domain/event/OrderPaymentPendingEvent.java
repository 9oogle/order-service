package com.goggles.orderservice.domain.event;

import com.goggles.orderservice.domain.model.Order;
import java.util.UUID;

public record OrderPaymentPendingEvent(
    UUID orderId,
    Long amount,
    UUID customerId,
    String customerName,
    String customerEmail,
    String orderName) {
  public static OrderPaymentPendingEvent from(Order order) {
    String buildOrderName;
    if (order.getItems().size() > 1) {
      buildOrderName =
          order.getItems().getFirst().getProduct().getProductName()
              + "외 "
              + (order.getItems().size() - 1)
              + "건";
    } else {
      buildOrderName = order.getItems().getFirst().getProduct().getProductName();
    }
    return new OrderPaymentPendingEvent(
        order.getId(),
        order.getPrice().getFinalPrice(),
        order.getOrderer().getStudentId(),
        order.getOrderer().getStudentName(),
        order.getOrderer().getStudentEmail(),
        buildOrderName);
  }
}
