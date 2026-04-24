package com.goggles.orderservice.domain.enums;

import java.util.EnumSet;
import java.util.Set;

public enum OrderItemStatus {
  ACTIVE {
    @Override
    public Set<OrderItemStatus> allowedTransitions() {
      return EnumSet.of(CANCELED);
    }
  },
  CANCELED {
    @Override
    public Set<OrderItemStatus> allowedTransitions() {
      return EnumSet.noneOf(OrderItemStatus.class);
    }
  };

  public abstract Set<OrderItemStatus> allowedTransitions();

  public boolean canTransitionTo(OrderItemStatus next) {
    return allowedTransitions().contains(next);
  }
}
