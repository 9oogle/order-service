package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelMentoringBookingRequest {
  private UUID mentoringBookingId;
  private String cancelReason;

  public static CancelMentoringBookingRequest of(CancelMentoringBookingData data) {
    return new CancelMentoringBookingRequest(data.mentoringBookingId(), data.cancelReason().name());
  }
}
