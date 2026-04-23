package com.goggles.orderservice.infrastructure.repository;

import com.goggles.orderservice.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
  @Query("SELECT o FROM Order o " +
      "LEFT JOIN FETCH o.items " +
      "WHERE o.id = :orderId AND o.orderer.studentId = :studentId")
  Optional<Order> findByIdAndStudentId(
      @Param("orderId") UUID orderId, @Param("studentId") UUID studentId);
}
