package com.goggles.orderservice.infrastructure.repository;

import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.repository.OrderPageQuery;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
  private final OrderJpaRepository orderJpaRepository;
  private final OrderQueryDslRepository orderQueryDslRepository;

  @Override
  public Optional<Order> getOrderByIdAndUserId(UUID orderId, UUID userId) {
    return orderJpaRepository.findByIdAndStudentId(orderId, userId);
  }

  @Override
  public Page<Order> getOrderPage(OrderPageQuery query) {
    return orderQueryDslRepository.getOrderPage(query);
  }
}
