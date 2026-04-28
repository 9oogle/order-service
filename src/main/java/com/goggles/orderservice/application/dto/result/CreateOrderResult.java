package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.model.Order;
import java.util.UUID;

public record CreateOrderResult(UUID orderId, UUID studentId) {
  public static CreateOrderResult from(Order order) {
    return new CreateOrderResult(
        order.getId(), order.getOrderer().getStudentId()
    );
  }
}
