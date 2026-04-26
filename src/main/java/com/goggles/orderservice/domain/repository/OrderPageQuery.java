package com.goggles.orderservice.domain.repository;

import com.goggles.orderservice.domain.model.OrderSortType;
import com.goggles.orderservice.domain.model.OrderStatus;
import java.util.UUID;

public record OrderPageQuery(
    UUID userId, OrderSortType orderSort, OrderStatus orderStatus, int page, int size) {}
