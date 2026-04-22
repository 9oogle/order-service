package com.goggles.orderservice.domain.vo;

import com.goggles.orderservice.domain.enums.OrderItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
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

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(name = "product_price", nullable = false)
  private BigDecimal productPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "product_type", nullable = false, length = 20)
  private OrderItemType productType;

  public Product(
      UUID productId, String productName, BigDecimal productPrice, OrderItemType productType) {
    validate(productId, productName, productPrice, productType);
    this.productId = productId;
    this.productName = productName;
    this.productPrice = productPrice;
    this.productType = productType;
  }

  private static void validate(
      UUID productId,
      String productName,
      BigDecimal productPrice,
      OrderItemType productType
  ) {
    if (productId == null) {
      throw new IllegalArgumentException("productId 값은 필수입니다.");
    }
    if (productName == null || productName.isBlank()) {
      throw new IllegalArgumentException("productName 값은 필수입니다.");
    }
    if (productPrice == null) {
      throw new IllegalArgumentException("price 값은 필수입니다.");
    }
    if (productPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("productPrice 0 미만일 수 없습니다.");
    }
    if (productType == null) {
      throw new IllegalArgumentException("productType 값은 필수입니다.");
    }
  }
}
