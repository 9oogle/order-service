package com.goggles.orderservice.domain.repository;

import com.goggles.orderservice.domain.model.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderRepository {
  Optional<Order> getOrderByIdAndUserId(UUID orderId, UUID userId);

  Page<Order> getOrderPage(OrderPageQuery query);

  Order createOrder(Order order);
}
