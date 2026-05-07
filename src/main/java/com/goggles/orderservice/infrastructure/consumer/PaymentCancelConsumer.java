package com.goggles.orderservice.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.orderservice.application.dto.command.CancelOrderPaymentCommand;
import com.goggles.orderservice.application.dto.command.FailOrderPaymentCommand;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.infrastructure.event.PaymentCancelEvent;
import com.goggles.orderservice.infrastructure.event.PaymentFailedEvent;
import com.goggles.orderservice.infrastructure.exception.InvalidPaymentEventPayloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancelConsumer {
  public static final String TOPIC = "payment.canceled";
  public static final String GROUP_NAME = "order-service.payment-canceled";

  private final OrderCommandService orderCommandService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record) {
    log.info(
        "[Kafka] Received {} | partition={}, offset={}",
        TOPIC,
        record.partition(),
        record.offset());

    orderCommandService.cancelOrderPayment(toCommand(record.value()));
  }

  private CancelOrderPaymentCommand toCommand(String value) {
    try {
      PaymentCancelEvent event =
          objectMapper.readValue(value, PaymentCancelEvent.class);
      return new CancelOrderPaymentCommand(
          event.orderId(), event.amount(), event.cancelAt(), event.cancelReason());
    } catch (JsonProcessingException e) {
      throw new InvalidPaymentEventPayloadException();
    }
  }
}
