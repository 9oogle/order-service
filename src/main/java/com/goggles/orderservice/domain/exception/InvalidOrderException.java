package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.BadRequestException;
import lombok.Getter;

@Getter
public class InvalidOrderException extends BadRequestException {

  private final OrderErrorCode errorCode;

  public InvalidOrderException(OrderErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public InvalidOrderException(OrderErrorCode errorCode, Object... args) {
    super(errorCode.getMessage(args));
    this.errorCode = errorCode;
  }
}
