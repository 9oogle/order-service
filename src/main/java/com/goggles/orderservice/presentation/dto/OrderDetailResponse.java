package com.goggles.orderservice.presentation.dto;

import com.goggles.orderservice.application.dto.result.OrderDetailResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
    UUID orderId,
    UUID studentId,
    String studentName,
    Long originalPrice,
    Long finalPrice,
    UUID couponId,
    String couponCode,
    String couponName,
    BigDecimal couponDiscountRate,
    String paymentKey,
    String paymentMethod,
    LocalDateTime orderDate,
    String orderStatus,
    String cancelReason,
    String cancelDescription,
    LocalDateTime canceledAt,
    List<OrderItemSummaryResponse> orderItems) {
  public static OrderDetailResponse from(OrderDetailResult result) {
    return new OrderDetailResponse(
        result.orderId(),
        result.orderer().getStudentId(),
        result.orderer().getStudentName(),
        result.price().getOriginalPrice(),
        result.price().getFinalPrice(),
        result.coupon() != null ? result.coupon().getCouponId() : null,
        result.coupon() != null ? result.coupon().getCouponCode() : null,
        result.coupon() != null ? result.coupon().getCouponName() : null,
        result.coupon() != null ? result.coupon().getCouponDiscountRate() : null,
        result.payment() != null ? result.payment().getPaymentKey() : null,
        result.payment() != null ? result.payment().getPaymentMethod() : null,
        result.createdAt(),
        result.status().getDisplayName(),
        result.cancelReason() != null ? result.cancelReason().name() : null,
        result.cancelDescription() != null ? result.cancelDescription() : null,
        result.canceledAt() != null ? result.canceledAt() : null,
        result.orderItems().stream().map(OrderItemSummaryResponse::from).toList());
  }
}
