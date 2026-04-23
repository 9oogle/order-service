package com.goggles.orderservice.application.dto.result;

import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.enums.OrderStatus;
import com.goggles.orderservice.domain.vo.Coupon;
import com.goggles.orderservice.domain.vo.OrderPrice;
import com.goggles.orderservice.domain.vo.Orderer;
import com.goggles.orderservice.domain.vo.Payment;
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
        order.getItems().stream().map(OrderItemSummary::from).toList());
  }
}
