package com.goggles.orderservice.application.dto.command;

import com.goggles.orderservice.domain.model.Instructor;
import com.goggles.orderservice.domain.model.Product;

public record CreateOrderItemCommand(Product product, Instructor instructor) {}
