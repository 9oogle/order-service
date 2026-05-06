package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.model.Order;
import java.util.UUID;

public record CancelOrderResult(UUID orderId, Long refundPrice) {
  public static CancelOrderResult from(Order order) {
    return new CancelOrderResult(order.getId(), order.getPrice().getFinalPrice());
  }
}
