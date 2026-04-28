package com.goggles.orderservice.domain.model;

import com.goggles.common.exception.BadRequestException;
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
      throw new BadRequestException("상품 타입은 필수입니다.");
    }
    return Arrays.stream(values())
        .filter(t -> t.name().equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("유효하지 않은 상품 타입입니다."));
  }
}
