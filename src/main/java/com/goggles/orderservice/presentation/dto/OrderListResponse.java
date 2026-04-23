package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.OrderListResult;
import com.goggles.orderservice.domain.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderListResponse(
    UUID orderId,
    Long originalPrice,
    Long finalPrice,
    LocalDateTime createdAt,
    OrderStatus status,
    List<OrderItemSummaryResponse> orderItems) {
  public static OrderListResponse from(OrderListResult result) {
    return new OrderListResponse(
        result.orderId(),
        result.price().getOriginalPrice(),
        result.price().getFinalPrice(),
        result.createdAt(),
        result.status(),
        result.orderItems().stream().map(OrderItemSummaryResponse::from).toList());
  }
}
