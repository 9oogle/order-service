package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
import java.util.Arrays;

public enum OrderType {
  LECTURE,
  MENTORING;

  public static OrderType from(String value) {
    if (value == null) {
      throw new InvalidOrderException(OrderErrorCode.MISSING_ORDER_TYPE);
    }
    return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new InvalidOrderException(OrderErrorCode.INVALID_ORDER_TYPE, value));
  }
}
