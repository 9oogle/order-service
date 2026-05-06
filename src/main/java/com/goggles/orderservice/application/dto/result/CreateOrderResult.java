package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.model.Order;
import java.util.UUID;

public record CreateOrderResult(UUID orderId, String productName, Long orderPrice) {
  public static CreateOrderResult from(Order order) {
    return new CreateOrderResult(
        order.getId(),
        order.getOrderName(),
        order.getPrice().getFinalPrice());
  }
}
