package com.goggles.orderservice.domain.model;

import com.goggles.common.exception.BadRequestException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;

@Getter
public enum OrderStatus {
  PAYMENT_PENDING("결제 대기") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(PAID, PAYMENT_FAILED, CANCEL_REQUESTED);
    }
  },
  PAID("결제 완료") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(COMPLETED, CANCEL_REQUESTED);
    }
  },
  PAID_CANCELED("결제 취소") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(CANCELED);
    }
  },
  COMPLETED("주문 완료") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(CANCEL_REQUESTED);
    }
  },
  PAYMENT_FAILED("결제 실패") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.noneOf(OrderStatus.class);
    }
  },
  CANCEL_REQUESTED("취소 요청") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.of(PAID_CANCELED);
    }
  },
  CANCELED("주문 취소") {
    @Override
    public Set<OrderStatus> allowedTransitions() {
      return EnumSet.noneOf(OrderStatus.class);
    }
  };

  private final String displayName;

  OrderStatus(String displayName) {
    this.displayName = displayName;
  }

  public abstract Set<OrderStatus> allowedTransitions();

  public boolean canTransitionTo(OrderStatus next) {
    return allowedTransitions().contains(next);
  }

  public static OrderStatus from(String value) {
    if (value == null) return null;
    try {
      return OrderStatus.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException(OrderErrorCode.INVALID_ORDER_STATUS + value);
    }
  }
}
