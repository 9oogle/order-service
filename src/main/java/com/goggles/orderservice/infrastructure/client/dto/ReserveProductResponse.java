package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveProductResponse {
  private UUID enrollmentId;
  private UUID productId;
  private String productName;
  private Long productPrice;
  private UUID instructorId;
  private String instructorName;

  public ProductReserveInfo toProductItem() {
    return new ProductReserveInfo(
        this.enrollmentId,
        this.productId,
        this.productName,
        this.productPrice,
        this.instructorId,
        this.instructorName);
  }
}
