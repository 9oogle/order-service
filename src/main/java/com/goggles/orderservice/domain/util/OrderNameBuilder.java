package com.goggles.orderservice.domain.util;

import com.goggles.orderservice.domain.model.OrderItem;
import java.util.List;

public class OrderNameBuilder {

  private OrderNameBuilder() {}

  public static String build(List<OrderItem> items) {
    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("주문 아이템이 비어있습니다.");
    }

    String firstName = items.getFirst().getProduct().getProductName();

    if (items.size() == 1) {
      return firstName;
    }

    return firstName + " 외 " + (items.size() - 1) + "건";
  }
}
