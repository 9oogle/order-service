package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;
import java.util.UUID;

public record OrderItemSpec(Product product, Instructor instructor, UUID enrollmentId) {
  public OrderItemSpec {
    if (product == null || instructor == null || enrollmentId == null) {
      throw new InvalidVoException(VoErrorCode.NULL_ORDER_ITEM_SPEC);
    }
  }
}
