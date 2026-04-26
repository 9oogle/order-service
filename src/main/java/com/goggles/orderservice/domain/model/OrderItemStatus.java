package com.goggles.orderservice.domain.model;

import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;

@Getter
public enum OrderItemStatus {
  ACTIVE("완료") {
    @Override
    public Set<OrderItemStatus> allowedTransitions() {
      return EnumSet.of(CANCELED);
    }
  },
  CANCELED("취소") {
    @Override
    public Set<OrderItemStatus> allowedTransitions() {
      return EnumSet.noneOf(OrderItemStatus.class);
    }
  };

  private final String displayName;

  OrderItemStatus(String displayName) {
    this.displayName = displayName;
  }

  public abstract Set<OrderItemStatus> allowedTransitions();

  public boolean canTransitionTo(OrderItemStatus next) {
    return allowedTransitions().contains(next);
  }
}
