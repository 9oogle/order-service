package com.goggles.orderservice.presentation.controller;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.application.dto.result.OrderListResult;
import com.goggles.orderservice.application.service.OrderQueryService;
import com.goggles.orderservice.presentation.dto.OrderDetailResponse;
import com.goggles.orderservice.presentation.dto.OrderListResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderQueryService orderQueryService;

  @GetMapping
  public CommonPageResponse<OrderListResponse> getOrders(
      @RequestHeader("X-User-Id") UUID userId,
      CommonPageRequest pageRequest,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) String orderStatus) {

    Page<OrderListResult> results =
        orderQueryService.getOrders(OrderListQuery.of(userId, sort, orderStatus, pageRequest));

    return CommonPageResponse.of(results, OrderListResponse::from);
  }

  @GetMapping("/{orderId}")
  public OrderDetailResponse getOrderDetails(
      @PathVariable("orderId") UUID orderId, @RequestHeader("X-User-Id") UUID userId) {
    return OrderDetailResponse.from(orderQueryService.getOrderDetails(orderId, userId));
  }
}
