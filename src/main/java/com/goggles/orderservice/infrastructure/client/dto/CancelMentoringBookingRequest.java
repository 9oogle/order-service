package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelMentoringBookingRequest {
  private String cancelReason;

  public static CancelMentoringBookingRequest of(CancelMentoringBookingData data) {
    return new CancelMentoringBookingRequest(data.cancelReason().name());
  }
}
