package com.goggles.orderservice.infrastructure.repository;

import com.goggles.orderservice.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID> {}
