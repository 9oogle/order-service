package com.goggles.orderservice.domain.vo;

import com.goggles.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPrice {

  @Column(name = "original_price", nullable = false)
  private BigDecimal originalPrice;

  @Column(name = "discount_amount", nullable = false)
  private BigDecimal discountAmount;

  @Column(name = "final_price", nullable = false)
  private BigDecimal finalPrice;

  public OrderPrice(BigDecimal originalPrice, BigDecimal discountAmount) {
    validate(originalPrice, discountAmount);
    this.originalPrice = originalPrice;
    this.discountAmount = discountAmount;
    this.finalPrice = originalPrice.subtract(discountAmount);
  }

  private static void validate(BigDecimal originalPrice, BigDecimal discountAmount) {
    if (originalPrice == null || discountAmount == null) {
      throw new BadRequestException("originalPrice 또는 discountAmount는 null일 수 없습니다.");
    }
    if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("originalPrice는 0 미만일 수 없습니다.");
    }
    if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
      throw new BadRequestException("discountAmount는 0 미만일 수 없습니다.");
    }
    if (discountAmount.compareTo(originalPrice) > 0) {
      throw new BadRequestException("finalPrice는 originalPrice를 초과할 수 없습니다.");
    }
  }
}