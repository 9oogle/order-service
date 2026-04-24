package com.goggles.orderservice.domain.enums;

import com.goggles.common.exception.BadRequestException;
import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
  PAYMENT_PENDING {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(PAID, PAYMENT_FAILED);
    }
  },
  PAID {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(COMPLETED);
    }
  },
  COMPLETED {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(CANCELED);
    }
  },
  PAYMENT_FAILED {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(CANCELED);
    }
  },
  CANCELED {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.noneOf(OrderStatus.class);
    }
  };

  public abstract Set<OrderStatus> allowedTransitions();

  public boolean canTransitionTo(OrderStatus next) {
    return allowedTransitions().contains(next);
  }

  public static OrderStatus from(String value) {
    if (value == null) return null;
    try {
      return OrderStatus.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("유효하지 않은 주문 상태입니다: " + value);
    }
  }
}
