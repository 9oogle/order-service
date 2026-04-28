package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse.ProductEnrollment;
import java.util.List;
import java.util.UUID;

public record LectureProductReserveInfo(List<LectureProduct> products, int count) {

  public static LectureProductReserveInfo from(ReserveProductResponse response) {
    return new LectureProductReserveInfo(
        response.getEnrollments().stream().map(LectureProduct::from).toList(),
        response.getCount());
  }

  record LectureProduct(
      UUID enrollmentId,
      UUID lectureId,
      String lectureName,
      Long lecturePrice,
      UUID instructorId,
      String instructorName) {
    public static LectureProduct from(ProductEnrollment lectureProduct) {
      return new LectureProduct(
          lectureProduct.getEnrollmentId(),
          lectureProduct.getProductId(),
          lectureProduct.getProductName(),
          lectureProduct.getProductPrice(),
          lectureProduct.getInstructorId(),
          lectureProduct.getInstructorName());
    }
  }
}
