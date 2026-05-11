package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;

public enum CancelReason {
  PAYMENT_FAIL,
  USER_CANCEL,
  SYSTEM_ERROR,
  MENTOR_CANCEL,
  INSTRUCTOR_CANCEL,
  TIMEOUT;

  public static CancelReason from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidOrderException(OrderErrorCode.MISSING_CANCEL_REASON);
    }
    try {
      return CancelReason.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidOrderException(OrderErrorCode.INVALID_CANCEL_REASON, value);
    }
  }
}
