package com.goggles.orderservice.application.exception;

import com.goggles.common.exception.InternalServerException;

public class ExternalServiceException extends InternalServerException {

  public ExternalServiceException(String message) {
    super(message);
  }
}
