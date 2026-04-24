package com.goggles.orderservice.domain.vo;

import com.goggles.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class Coupon {
  @Column(name = "coupon_id")
  private UUID couponId;

  @Column(name = "coupon_code")
  private String couponCode;

  @Column(name = "coupon_name", length = 100)
  private String couponName;

  @Column(name = "coupon_discount_rate", precision = 5, scale = 2)
  private BigDecimal couponDiscountRate;

  public Coupon(
      UUID couponId, String couponCode, String couponName, BigDecimal couponDiscountRate) {
    validate(couponId, couponCode, couponName, couponDiscountRate);
    this.couponId = couponId;
    this.couponCode = couponCode;
    this.couponName = couponName;
    this.couponDiscountRate = couponDiscountRate;
  }

  private static void validate(
      UUID couponId, String couponCode, String couponName, BigDecimal couponDiscountRate) {
    boolean hasId = couponId != null;
    boolean hasCode = couponCode != null && !couponCode.isBlank();
    boolean hasName = couponName != null && !couponName.isBlank();

    if (hasId != hasCode || hasId != hasName) {
      throw new BadRequestException("couponId, couponCode, couponName은 함께 존재하거나 함께 없어야 합니다.");
    }

    if (hasId && couponDiscountRate == null) {
      throw new BadRequestException("쿠폰이 존재할 경우 couponDiscountRate도 필수입니다.");
    }
  }
}
