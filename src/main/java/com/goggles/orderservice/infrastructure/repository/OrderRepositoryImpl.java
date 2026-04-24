package com.goggles.orderservice.infrastructure.repository;

import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
  private final OrderJpaRepository orderJpaRepository;

  @Override
  public Optional<Order> getOrderByIdAndUserId(UUID orderId, UUID userId) {
    return orderJpaRepository.findByIdAndStudentId(orderId, userId);
  }
}
