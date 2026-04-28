package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.domain.model.Instructor;
import com.goggles.orderservice.domain.model.OrderItemSpec;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.Product;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse.ProductEnrollment;
import java.util.List;
import java.util.UUID;

public record ProductReserveInfo(List<ProductItem> products) {

  public static ProductReserveInfo from(ReserveProductResponse response) {
    return new ProductReserveInfo(
        response.getEnrollments().stream().map(ProductItem::from).toList());
  }

  public record ProductItem(
      UUID enrollmentId,
      UUID productId,
      String productName,
      Long productPrice,
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

    public OrderItemSpec toOrderItemSpec(OrderItemType type) {
      return new OrderItemSpec(
          new Product(productId(), productName(), productPrice(), type),
          new Instructor(instructorId(), instructorName()));
    }
  }
}
