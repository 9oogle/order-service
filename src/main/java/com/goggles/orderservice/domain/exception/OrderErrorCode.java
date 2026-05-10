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
  DUPLICATE_ORDER_ITEM("동일한 상품은 중복 추가할 수 없습니다."),
  ALREADY_CANCELED_ORDER_ITEM("이미 취소된 주문 상품입니다."),
  MISSING_ORDER_ITEM_TYPE("상품 타입은 필수입니다."),
  INVALID_ORDER_ITEM_TYPE("유효하지 않은 상품 타입입니다: "),

  // Order
  MISSING_ORDER_ORDERER("주문자 정보는 필수입니다."),
  MISSING_ORDER_ORDER_PRICE("주문 가격은 필수입니다."),
  INVALID_ORDER_AMOUNT("주문 금액이 맞지 않습니다."),
  INVALID_ORDER_STATUS("유효하지 않은 주문 상태입니다: "),
  ORDER_CANNOT_BE_CANCELLED("취소 불가능한 주문입니다."),
  MISSING_ORDER_ORDER_EVENTS("주문 이벤트는 필수입니다."),
  MISSING_ORDER_TYPE("주문 타입은 필수입니다."),
  INVALID_ORDER_TYPE("유효하지 않은 주문 타입입니다: "),
  MISSING_CANCEL_REASON("취소 원인은 필수입니다."),
  INVALID_CANCEL_DESCRIPTION("주문 취소 상세 이유는 100자를 넘을 수 없습니다."),
  INVALID_CANCEL_REASON("유효하지 않은 취소 원인입니다: "),
  INVALID_ORDER_SORT_TYPE("유효하지 않은 정렬 조건입니다: ");

  private final String messageTemplate;

  OrderErrorCode(String messageTemplate) {
    this.messageTemplate = messageTemplate;
  }

  public String getMessage(Object... args) {
    return args.length == 0 ? messageTemplate : messageTemplate.formatted(args);
  }
}
