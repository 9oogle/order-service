package com.goggles.orderservice.domain.exception;

import jakarta.ws.rs.BadRequestException;

public class InvalidVoException extends BadRequestException {

  public InvalidVoException(VoErrorCode voErrorCode) {
    super(voErrorCode.getMessage());
  }
}
