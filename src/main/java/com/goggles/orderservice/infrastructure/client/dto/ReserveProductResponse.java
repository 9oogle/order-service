package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import java.util.UUID;

public record ReserveProductResponse(
    UUID enrollmentId,
    UUID productId,
    String productName,
    Long productPrice,
    UUID instructorId,
    String instructorName) {
  public ProductReserveInfo toProductItem() {
    return new ProductReserveInfo(
        enrollmentId, productId, productName, productPrice, instructorId, instructorName);
  }
}
