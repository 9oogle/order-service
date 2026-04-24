package com.goggles.orderservice.infrastructure.repository.custom;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderCustomRepository {
  Page<Order> getOrderPage(OrderListQuery orderListQuery);
}
