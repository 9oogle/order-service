package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.domain.vo.Instructor;
import com.goggles.orderservice.domain.vo.Product;

public record CreateOrderItemCommand(
    Product product, Instructor instructor
) {}
