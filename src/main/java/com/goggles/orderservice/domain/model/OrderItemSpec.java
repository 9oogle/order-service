package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;

public record OrderItemSpec(Product product, Instructor instructor) {
  public OrderItemSpec {
    if (product == null || instructor == null) {
      throw new InvalidVoException(VoErrorCode.NULL_ORDER_ITEM_SPEC);
    }
  }
}
