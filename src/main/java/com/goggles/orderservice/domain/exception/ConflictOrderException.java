package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.BadRequestException;

public class ConflictOrderException extends BadRequestException {

  public ConflictOrderException(OrderErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
