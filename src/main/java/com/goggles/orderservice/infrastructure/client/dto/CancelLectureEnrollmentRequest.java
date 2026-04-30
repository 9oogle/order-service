package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.CancelLectureEnrollmentData;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelLectureEnrollmentRequest {
  private List<UUID> mentoringBookingIds;
  private String cancelReason;

  public static CancelLectureEnrollmentRequest of(CancelLectureEnrollmentData data) {
    return new CancelLectureEnrollmentRequest(
        data.mentoringBookingIds(),
        data.cancelReason().name()
    );
  }
}
