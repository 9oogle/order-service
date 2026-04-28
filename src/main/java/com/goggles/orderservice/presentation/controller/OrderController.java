package com.goggles.orderservice.presentation.controller;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.application.dto.result.OrderListResult;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.application.service.OrderQueryService;
import com.goggles.orderservice.presentation.dto.CreateLectureOrderRequest;
import com.goggles.orderservice.presentation.dto.CreateMentoringOrderRequest;
import com.goggles.orderservice.presentation.dto.CreateOrderResponse;
import com.goggles.orderservice.presentation.dto.OrderDetailResponse;
import com.goggles.orderservice.presentation.dto.OrderListResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderQueryService orderQueryService;
  private final OrderCommandService orderCommandService;

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

  @PostMapping("/mentoring")
  public CreateOrderResponse createMentoringOrder(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @Valid @RequestBody CreateMentoringOrderRequest request) {
    return CreateOrderResponse.from(
        orderCommandService.createMentoringOrder(request.toCommand(userId, userRole)));
  }

  @PostMapping("/lecture")
  public CreateOrderResponse createLectureOrder(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @Valid @RequestBody CreateLectureOrderRequest request) {
    return CreateOrderResponse.from(
        orderCommandService.createLectureOrder(
            request.toCommand(userId, userRole)));
  }
}
