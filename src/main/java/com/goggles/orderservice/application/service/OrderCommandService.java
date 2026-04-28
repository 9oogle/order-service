package com.goggles.orderservice.application.service;

import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.result.CreateOrderResult;

public interface OrderCommandService {
  CreateOrderResult createLectureOrder(CreateLectureOrderCommand command);
  CreateOrderResult createMentoringOrder(CreateMentoringOrderCommand command);
}
