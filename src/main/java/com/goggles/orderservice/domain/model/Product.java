package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
  @Column(name = "product_id", nullable = false, updatable = false)
  private UUID productId;

  @Column(name = "enrollment_id", nullable = false, updatable = false)
  private UUID enrollmentId;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "product_price", nullable = false)
  private Long productPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "product_type", nullable = false, length = 20)
  private OrderItemType productType;

  public Product(
      UUID productId,
      UUID enrollmentId,
      String productName,
      Long productPrice,
      OrderItemType productType) {
    validate(productId, productName, productPrice, productType);
    this.productId = productId;
    this.enrollmentId = enrollmentId;
    this.productName = productName;
    this.productPrice = productPrice;
    this.productType = productType;
  }

  private static void validate(
      UUID productId, String productName, Long productPrice, OrderItemType productType) {
    if (productId == null) {
      throw new InvalidVoException(VoErrorCode.MISSING_PRODUCT_ID);
    }
    if (productName == null || productName.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_PRODUCT_NAME);
    }
    if (productPrice == null) {
      throw new InvalidVoException(VoErrorCode.MISSING_PRODUCT_PRICE);
    }
    if (productPrice < 0) {
      throw new InvalidVoException(VoErrorCode.INVALID_PRODUCT_PRICE);
    }
    if (productType == null) {
      throw new InvalidVoException(VoErrorCode.MISSING_PRODUCT_TYPE);
    }
  }
}
