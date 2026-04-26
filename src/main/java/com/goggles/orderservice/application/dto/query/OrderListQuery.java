package com.goggles.orderservice.application.dto.query;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.orderservice.domain.model.OrderSortType;
import com.goggles.orderservice.domain.model.OrderStatus;
import java.util.UUID;

public record OrderListQuery(
    UUID userId, OrderSortType orderSort, OrderStatus orderStatus, CommonPageRequest pageRequest) {
  public static OrderListQuery of(
      UUID userId, String sort, String orderStatus, CommonPageRequest pageRequest) {
    return new OrderListQuery(
        userId, OrderSortType.from(sort), OrderStatus.from(orderStatus), pageRequest);
  }
}
