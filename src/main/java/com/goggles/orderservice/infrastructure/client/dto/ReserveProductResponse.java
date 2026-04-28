package com.goggles.orderservice.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveProductResponse {
  private List<ProductEnrollment> enrollments;
  private int count;

  @Getter
  @NoArgsConstructor
  public static class ProductEnrollment {
    private UUID enrollmentId;
    private UUID productId;
    private String productName;
    private Long productPrice;
    private UUID instructorId;
    private String instructorName;
  }
}
