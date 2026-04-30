package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.application.common.CancelReason;
import com.goggles.orderservice.domain.model.Coupon;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.OrderStatus;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.model.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResult(
    UUID orderId,
    Orderer orderer,
    Coupon coupon,
    OrderPrice price,
    Payment payment,
    LocalDateTime createdAt,
    OrderStatus status,
    CancelReason cancelReason,
    String cancelDescription,
    LocalDateTime canceledAt,
    List<OrderItemSummary> orderItems) {
  public static OrderDetailResult from(Order order) {
    return new OrderDetailResult(
        order.getId(),
        order.getOrderer(),
        order.getCoupon(),
        order.getPrice(),
        order.getPayment(),
        order.getCreatedAt(),
        order.getStatus(),
        order.getCancelReason(),
        order.getCancelDescription(),
        order.getCanceledAt(),
        order.getItems().stream().map(OrderItemSummary::from).toList());
  }
}
