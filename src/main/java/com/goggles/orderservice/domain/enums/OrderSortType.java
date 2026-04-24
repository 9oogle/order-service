package com.goggles.orderservice.domain.enums;

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
        .orElse(CREATED_DESC);
  }
}
