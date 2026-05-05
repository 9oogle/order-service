package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.CreateOrderResult;
import java.util.UUID;

public record CreateOrderResponse(UUID orderId, Long orderPrice, String productName) {
  public static CreateOrderResponse from(CreateOrderResult result) {
    return new CreateOrderResponse(result.orderId(), result.orderPrice(), result.productName());
  }
}
