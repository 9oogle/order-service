package com.goggles.orderservice.infrastructure.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidPaymentEventPayloadException extends BadRequestException {

  public InvalidPaymentEventPayloadException() {
    super("결제 이벤트 payload가 올바르지 않습니다.");
  }
}
