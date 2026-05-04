package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.model.Instructor;
import com.goggles.orderservice.domain.model.OrderItem;
import com.goggles.orderservice.domain.model.OrderItemStatus;
import com.goggles.orderservice.domain.model.Product;
import java.util.UUID;

public record OrderItemSummary(
    UUID orderItemId,
    Product product,
    Instructor instructor,
    OrderItemStatus status,
    UUID enrollmentId) {
  public static OrderItemSummary from(OrderItem item) {
    return new OrderItemSummary(
        item.getId(),
        item.getProduct(),
        item.getInstructor(),
        item.getStatus(),
        item.getEnrollmentId());
  }
}
