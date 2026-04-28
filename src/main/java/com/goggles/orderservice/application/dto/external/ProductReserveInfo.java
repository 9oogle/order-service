package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse.ProductEnrollment;
import java.util.List;
import java.util.UUID;

public record ProductReserveInfo(List<ProductItem> products, int count) {

  public static ProductReserveInfo from(ReserveProductResponse response) {
    return new ProductReserveInfo(
        response.getEnrollments().stream().map(ProductItem::from).toList(),
        response.getCount());
  }

  record ProductItem(
      UUID enrollmentId,
      UUID lectureId,
      String lectureName,
      Long lecturePrice,
      UUID instructorId,
      String instructorName) {
    public static ProductItem from(ProductEnrollment productEnrollment) {
      return new ProductItem(
          productEnrollment.getEnrollmentId(),
          productEnrollment.getProductId(),
          productEnrollment.getProductName(),
          productEnrollment.getProductPrice(),
          productEnrollment.getInstructorId(),
          productEnrollment.getInstructorName());
    }
  }
}
