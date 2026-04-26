package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.NotFoundException;

public class NotFoundOrderItemException extends NotFoundException {

  public NotFoundOrderItemException() {
    super("존재하지 않는 주문 상품입니다.");
  }
}
