package com.goggles.orderservice.domain.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidVoException extends BadRequestException {

  public InvalidVoException(VoErrorCode voErrorCode) {
    super(voErrorCode.getMessage());
  }
}
