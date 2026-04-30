package com.goggles.orderservice.application.common;

import com.goggles.common.exception.BadRequestException;

public enum CancelReason {
  PAYMENT_FAIL,
  USER_CANCEL,
  SYSTEM_ERROR,
  MENTOR_CANCEL,
  INSTRUCTOR_CANCEL,
  TIMEOUT;

  public static CancelReason from(String value) {
    if (value == null || value.isBlank()) throw new BadRequestException("취소 원인은 필수입니다.");
    try {
      return CancelReason.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("유효하지 취소 원인입니다: " + value);
    }
  }
}
