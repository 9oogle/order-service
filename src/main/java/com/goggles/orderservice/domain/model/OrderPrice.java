package com.goggles.orderservice.domain.model;

import com.goggles.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
  private Long originalPrice;

  @Column(name = "discount_amount", nullable = false)
  private Long discountAmount;

  @Column(name = "final_price", nullable = false)
  private Long finalPrice;

  public OrderPrice(Long originalPrice, Long discountAmount) {
    validate(originalPrice, discountAmount);
    this.originalPrice = originalPrice;
    this.discountAmount = discountAmount;
    this.finalPrice = originalPrice - discountAmount;
  }

  private static void validate(Long originalPrice, Long discountAmount) {
    if (originalPrice == null || discountAmount == null) {
      throw new BadRequestException("originalPrice 또는 discountAmount는 null일 수 없습니다.");
    }
    if (originalPrice < 0) {
      throw new BadRequestException("originalPrice는 0 미만일 수 없습니다.");
    }
    if (discountAmount < 0) {
      throw new BadRequestException("discountAmount는 0 미만일 수 없습니다.");
    }
    if (discountAmount > originalPrice) {
      throw new BadRequestException("discountAmount는 originalPrice를 초과할 수 없습니다.");
    }
  }
}
