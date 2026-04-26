package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderListResult(
    UUID orderId,
    OrderPrice price,
    LocalDateTime createdAt,
    OrderStatus status,
    List<OrderItemSummary> orderItems) {
  public static OrderListResult from(Order order) {
    return new OrderListResult(
        order.getId(),
        order.getPrice(),
        order.getCreatedAt(),
        order.getStatus(),
        order.getItems().stream().map(OrderItemSummary::from).toList());
  }
}
