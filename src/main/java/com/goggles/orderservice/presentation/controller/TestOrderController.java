package com.goggles.orderservice.presentation.controller;

import com.goggles.orderservice.application.dto.command.CancelOrderPaymentCommand;
import com.goggles.orderservice.application.dto.command.CompleteOrderPaymentCommand;
import com.goggles.orderservice.application.dto.command.FailOrderPaymentCommand;
import com.goggles.orderservice.application.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/orders")
public class TestOrderController {

  private final OrderCommandService orderCommandService;

  @PostMapping("/complete")
  public void completeOrderPaymentTest(@RequestBody CompleteOrderPaymentCommand command) {
    orderCommandService.completeOrderPayment(command);
  }

  @PostMapping("/fail")
  public void failOrderPaymentTest(@RequestBody FailOrderPaymentCommand command) {
    orderCommandService.failOrderPayment(command);
  }

  @PostMapping("/cancel")
  public void cancelOrderPaymentTest(@RequestBody CancelOrderPaymentCommand command) {
    orderCommandService.cancelOrderPayment(command);
  }
}
