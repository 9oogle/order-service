package com.goggles.orderservice.domain.repository;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.domain.enums.OrderSortType;
import com.goggles.orderservice.domain.enums.OrderStatus;
import java.util.UUID;

public record OrderPageQuery(
    UUID userId, OrderSortType orderSort, OrderStatus orderStatus, int page, int size) {
  public static OrderPageQuery from(OrderListQuery query) {
    return new OrderPageQuery(
        query.userId(),
        query.orderSort(),
        query.orderStatus(),
        query.pageRequest().getPage(),
        query.pageRequest().getSize());
  }
}
