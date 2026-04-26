package com.goggles.orderservice.domain.model;

import lombok.Getter;

@Getter
public enum OrderItemType {
  COURSE("강의"),
  MENTORING("멘토링");

  private final String displayName;

  OrderItemType(String displayName) {
    this.displayName = displayName;
  }
}
