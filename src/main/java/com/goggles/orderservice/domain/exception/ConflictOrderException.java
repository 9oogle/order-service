package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.ConflictException;

public class ConflictOrderException extends ConflictException {

  public ConflictOrderException(OrderErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
