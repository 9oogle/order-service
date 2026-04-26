package com.goggles.orderservice.domain.model;

import jakarta.ws.rs.BadRequestException;
import java.util.Arrays;

public enum OrderSortType {
  CREATED_DESC("createdAt,desc"),
  CREATED_ASC("createdAt,asc"),
  PRICE_DESC("price,desc"),
  PRICE_ASC("price,asc");

  private final String value;

  OrderSortType(String value) {
    this.value = value;
  }

  public static OrderSortType from(String value) {
    if (value == null) return CREATED_DESC;
    return Arrays.stream(values())
        .filter(t -> t.value.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("유효하지 않은 정렬 조건입니다."));
  }
}
