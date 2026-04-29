package com.goggles.orderservice.domain.exception;

import lombok.Getter;

@Getter
public enum VoErrorCode {
  // CouponInfo
  INVALID_COUPON_FIELDS("couponId, couponCode, couponName은 함께 존재하거나 함께 없어야 합니다."),
  MISSING_COUPON_DISCOUNT_RATE("쿠폰이 존재할 경우 couponDiscountRate도 필수입니다."),

  // InstructorInfo
  MISSING_INSTRUCTOR_ID("instructorId 값은 필수입니다."),
  MISSING_INSTRUCTOR_NAME("instructorName 값은 필수입니다."),

  // StudentInfo
  MISSING_STUDENT_ID("studentId 값은 필수입니다."),
  MISSING_STUDENT_NAME("studentName 값은 필수입니다."),

  // PaymentInfo
  MISSING_PAYMENT_KEY("paymentKey 값은 필수입니다."),
  MISSING_PAYMENT_NAME("paymentMethod 값은 필수입니다."),

  // ProductInfo
  MISSING_PRODUCT_ID("productId 값은 필수입니다."),
  MISSING_PRODUCT_NAME("productName 값은 필수입니다."),
  MISSING_PRODUCT_PRICE("productPrice 값은 필수입니다."),
  INVALID_PRODUCT_PRICE("productPrice는 0 미만일 수 없습니다."),
  MISSING_PRODUCT_TYPE("productType 값은 필수입니다."),

  // PriceInfo
  MISSING_PRICE_FIELDS("originalPrice 또는 discountAmount는 null일 수 없습니다."),
  INVALID_ORIGINAL_PRICE("originalPrice는 0 미만일 수 없습니다."),
  INVALID_DISCOUNT_AMOUNT("discountAmount는 0 미만일 수 없습니다."),
  DISCOUNT_EXCEEDS_ORIGINAL("discountAmount는 originalPrice를 초과할 수 없습니다."),

  // OrderItemSpec
  NULL_ORDER_ITEM_SPEC("주문 상품 정보는 null일 수 없습니다.");

  private final String message;

  VoErrorCode(String message) {
    this.message = message;
  }
}
