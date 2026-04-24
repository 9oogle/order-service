package com.goggles.orderservice.domain.repository;

import com.goggles.orderservice.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Optional<Order> getOrderByIdAndUserId(UUID orderId, UUID userId);
}
