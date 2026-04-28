package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.CreateOrderResult;
import java.util.UUID;

public record CreateOrderResponse(UUID orderId, UUID studentId) {
  public static CreateOrderResponse from(CreateOrderResult result) {
    return new CreateOrderResponse(result.orderId(), result.studentId());
  }
}
