package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.ConflictException;

public class DuplicateOrderItemException extends ConflictException {

  public DuplicateOrderItemException() {
    super("동일한 상품은 중복 추가할 수 없습니다.");
  }
}
