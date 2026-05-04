package com.goggles.orderservice.domain.exception;

import lombok.Getter;

@Getter
public enum OrderErrorCode {
  // statusTransition
  INVALID_STATUS_TRANSITION("주문 상태를 %s에서 %s로 변경할 수 없습니다."),

  // orderItem
  EMPTY_ORDER_ITEMS("주문 상품은 최소 1개 이상이어야 합니다."),
  NULL_ORDER_ITEM("주문 상품에 null 값이 포함될 수 없습니다."),
  ALREADY_ASSIGNED_ORDER("이미 주문에 속한 상품입니다."),
  ALREADY_CANCELED_ORDER_ITEM("이미 취소된 주문 상품입니다."),

  // Order
  MISSING_ORDER_ORDERER("주문자 정보는 필수입니다."),
  MISSING_ORDER_ORDER_PRICE("주문 가격은 필수입니다."),
  MISSING_ORDER_ORDER_EVENTS("주문 이벤트는 필수입니다.");

  private final String messageTemplate;

  OrderErrorCode(String messageTemplate) {
    this.messageTemplate = messageTemplate;
  }

  public String getMessage(Object... args) {
    return args.length == 0 ? messageTemplate : messageTemplate.formatted(args);
  }
}
