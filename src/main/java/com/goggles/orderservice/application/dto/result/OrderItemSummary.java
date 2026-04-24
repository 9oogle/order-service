package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.entity.OrderItem;
import com.goggles.orderservice.domain.enums.OrderItemStatus;
import com.goggles.orderservice.domain.vo.Instructor;
import com.goggles.orderservice.domain.vo.Product;
import java.util.UUID;

public record OrderItemSummary(
    UUID orderItemId, Product product, Instructor instructor, OrderItemStatus status) {
  public static OrderItemSummary from(OrderItem item) {
    return new OrderItemSummary(
        item.getId(), item.getProduct(), item.getInstructor(), item.getStatus());
  }
}
