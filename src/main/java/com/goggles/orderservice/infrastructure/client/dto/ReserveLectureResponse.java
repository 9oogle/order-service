package com.goggles.orderservice.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveLectureResponse {
  private List<LectureEnrollment> lectureEnrollments;
  private int count;

  @Getter
  @NoArgsConstructor
  public static class LectureEnrollment {
    private UUID enrollmentId;
    private UUID lectureId;
    private String lectureName;
    private Long lecturePrice;
    private UUID instructorId;
    private String instructorName;
  }
}
