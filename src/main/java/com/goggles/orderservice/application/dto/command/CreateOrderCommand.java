package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.presentation.dto.CreateOrderRequest;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
    UUID studentId, UUID couponId, String paymentMethod, List<CreateOrderItem> items) {
  public record CreateOrderItem(UUID productId, String productType) {}

  public static CreateOrderCommand from(CreateOrderRequest request, UUID studentId) {
    return new CreateOrderCommand(
        studentId,
        request.couponId(),
        request.paymentMethod(),
        request.items().stream()
            .map(i -> new CreateOrderItem(i.productId(), i.productType()))
            .toList());
  }
}
