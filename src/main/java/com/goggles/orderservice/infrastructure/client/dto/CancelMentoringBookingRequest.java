package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import java.util.UUID;

public record CancelMentoringBookingRequest(
    UUID orderId, String cancelReason, String cancelDescription) {
  public static CancelMentoringBookingRequest from(CancelMentoringBookingData data) {
    return new CancelMentoringBookingRequest(
        data.orderId(), data.cancelReason(), data.cancelDescription());
  }
}
