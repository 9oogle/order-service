package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.NotFoundException;

public class NotFoundOrderException extends NotFoundException {

  public NotFoundOrderException() {
    super("주문을 찾을 수 없습니다.");
  }
}
