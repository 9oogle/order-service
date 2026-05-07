package com.goggles.orderservice.domain.model;

import com.goggles.common.exception.BadRequestException;
import java.util.Arrays;

public enum OrderType {
  LECTURE,
  MENTORING;

  public static OrderType from(String value) {
    if (value == null) {
      throw new BadRequestException("주문 타입은 필수입니다.");
    }
    return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("유효하지 않은 주문 타입입니다."));
  }
}
