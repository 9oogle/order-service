package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;
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
      throw new InvalidVoException(VoErrorCode.MISSING_PRICE_FIELDS);
    }
    if (originalPrice < 0) {
      throw new InvalidVoException(VoErrorCode.INVALID_ORIGINAL_PRICE);
    }
    if (discountAmount < 0) {
      throw new InvalidVoException(VoErrorCode.INVALID_DISCOUNT_AMOUNT);
    }
    if (discountAmount > originalPrice) {
      throw new InvalidVoException(VoErrorCode.DISCOUNT_EXCEEDS_ORIGINAL);
    }
  }
}
