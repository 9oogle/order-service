package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureResponse;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureResponse.LectureEnrollment;
import java.util.List;
import java.util.UUID;

public record LectureProductReserveInfo(List<LectureProduct> products, int count) {

  public static LectureProductReserveInfo from(ReserveLectureResponse response) {
    return new LectureProductReserveInfo(
        response.getLectureEnrollments().stream().map(LectureProduct::from).toList(),
        response.getCount());
  }

  record LectureProduct(
      UUID enrollmentId,
      UUID lectureId,
      String lectureName,
      Long lecturePrice,
      UUID instructorId,
      String instructorName) {
    public static LectureProduct from(LectureEnrollment lectureProduct) {
      return new LectureProduct(
          lectureProduct.getEnrollmentId(),
          lectureProduct.getLectureId(),
          lectureProduct.getLectureName(),
          lectureProduct.getLecturePrice(),
          lectureProduct.getInstructorId(),
          lectureProduct.getInstructorName());
    }
  }
}
