package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.OrderItemSummary;
import java.util.UUID;

public record OrderItemSummaryResponse(
    UUID orderItemId,
    UUID productId,
    String productName,
    Long productPrice,
    String productType,
    UUID instructorId,
    String instructorName,
    String orderItemStatus) {
  public static OrderItemSummaryResponse from(OrderItemSummary item) {
    return new OrderItemSummaryResponse(
        item.orderItemId(),
        item.product().getProductId(),
        item.product().getProductName(),
        item.product().getProductPrice(),
        item.product().getProductType().name(),
        item.instructor().getInstructorId(),
        item.instructor().getInstructorName(),
        item.status().getDisplayName());
  }
}
