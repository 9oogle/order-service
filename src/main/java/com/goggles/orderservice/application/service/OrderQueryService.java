package com.goggles.orderservice.application.service;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.application.dto.result.OrderDetailResult;
import com.goggles.orderservice.application.dto.result.OrderListResult;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderQueryService {
  OrderDetailResult getOrderDetails(UUID orderId, UUID userId);

  Page<OrderListResult> getOrders(OrderListQuery orderListQuery);
}
