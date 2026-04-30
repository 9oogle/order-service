package com.goggles.orderservice.infrastructure.client.exception;

import com.goggles.common.exception.InternalServerException;

public class ExternalServiceException extends InternalServerException {

  public ExternalServiceException(String message) {
    super(message);
  }
}
