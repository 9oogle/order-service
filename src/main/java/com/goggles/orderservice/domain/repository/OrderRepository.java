package com.goggles.orderservice.domain.repository;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderRepository {
  Optional<Order> getOrderByIdAndUserId(UUID orderId, UUID userId);

  Page<Order> getOrderPage(OrderListQuery query);
}
