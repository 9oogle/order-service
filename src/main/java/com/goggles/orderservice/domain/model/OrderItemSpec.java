package com.goggles.orderservice.domain.model;

import com.goggles.common.exception.BadRequestException;

public record OrderItemSpec(Product product, Instructor instructor) {
  public OrderItemSpec {
    if (product == null || instructor == null) {
      throw new BadRequestException("주문 상품 정보는 null일 수 없습니다.");
    }
  }
}
