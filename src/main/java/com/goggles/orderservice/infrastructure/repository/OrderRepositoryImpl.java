package com.goggles.orderservice.infrastructure.repository;

import com.goggles.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
  private final OrderJpaRepository orderJpaRepository;

}
