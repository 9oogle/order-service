package com.goggles.orderservice.application.service.impl;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.application.dto.result.OrderDetailResult;
import com.goggles.orderservice.application.dto.result.OrderListResult;
import com.goggles.orderservice.application.service.OrderQueryService;
import com.goggles.orderservice.domain.exception.NotFoundOrderException;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.repository.OrderPageQuery;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderQueryServiceImpl implements OrderQueryService {
  private final OrderRepository orderRepository;

  @Override
  public OrderDetailResult getOrderDetails(UUID orderId, UUID userId) {
    Order order =
        orderRepository
            .getOrderByIdAndUserId(orderId, userId)
            .orElseThrow(NotFoundOrderException::new);

    return OrderDetailResult.from(order);
  }

  @Override
  public Page<OrderListResult> getOrders(OrderListQuery orderListQuery) {
    OrderPageQuery query =
        new OrderPageQuery(
            orderListQuery.userId(),
            orderListQuery.orderSort(),
            orderListQuery.orderStatus(),
            orderListQuery.pageRequest().getPage(),
            orderListQuery.pageRequest().getSize());
    Page<Order> orderPage = orderRepository.getOrderPage(query);
    return orderPage.map(OrderListResult::from);
  }
}
