package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.CancelOrderResult;
import java.util.UUID;

public record CancelOrderResponse(UUID orderId, Long refundPrice) {
  public static CancelOrderResponse from(CancelOrderResult result) {
    return new CancelOrderResponse(result.orderId(), result.refundPrice());
  }
}
