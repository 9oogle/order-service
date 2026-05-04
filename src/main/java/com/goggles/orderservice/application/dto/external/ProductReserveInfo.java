package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.domain.model.Instructor;
import com.goggles.orderservice.domain.model.OrderItemSpec;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.Product;
import java.util.UUID;

public record ProductReserveInfo(
    UUID enrollmentId,
    UUID productId,
    String productName,
    Long productPrice,
    UUID instructorId,
    String instructorName) {

  public OrderItemSpec toOrderItemSpec(OrderItemType type) {
    return new OrderItemSpec(
        new Product(productId(), productName(), productPrice(), type),
        new Instructor(instructorId(), instructorName()),
        enrollmentId
    );
  }
}
