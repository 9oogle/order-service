package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum OrderItemType {
  LECTURE("강의"),
  MENTORING("멘토링");

  private final String displayName;

  OrderItemType(String displayName) {
    this.displayName = displayName;
  }

  public static OrderItemType from(String value) {
    if (value == null) {
      throw new InvalidOrderException(OrderErrorCode.MISSING_ORDER_ITEM_TYPE);
    }
    return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(
            () -> new InvalidOrderException(OrderErrorCode.INVALID_ORDER_ITEM_TYPE, value));
  }
}
