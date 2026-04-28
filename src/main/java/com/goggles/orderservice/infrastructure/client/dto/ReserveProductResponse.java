package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveProductResponse {
  private List<ProductEnrollment> enrollments;

  public ProductReserveInfo toProductReserveInfo() {
    return new ProductReserveInfo(
        this.enrollments.stream().map(ProductEnrollment::toProductItem).toList());
  }

  @Getter
  @NoArgsConstructor
  public static class ProductEnrollment {
    private UUID enrollmentId;
    private UUID productId;
    private String productName;
    private Long productPrice;
    private UUID instructorId;
    private String instructorName;

    public ProductReserveInfo.ProductItem toProductItem() {
      return new ProductReserveInfo.ProductItem(
          this.enrollmentId,
          this.productId,
          this.productName,
          this.productPrice,
          this.instructorId,
          this.instructorName);
    }
  }
}
