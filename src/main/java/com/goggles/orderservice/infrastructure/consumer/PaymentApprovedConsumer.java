package com.goggles.orderservice.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.orderservice.application.dto.command.CompleteOrderPaymentCommand;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.infrastructure.event.PaymentApprovedEvent;
import com.goggles.orderservice.infrastructure.exception.InvalidPaymentEventPayloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentApprovedConsumer {
  public static final String TOPIC = "payment.approved";
  public static final String GROUP_NAME = "order-service.payment-approved";

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

    orderCommandService.completeOrderPayment(toCommand(record.value()));
  }

  private CompleteOrderPaymentCommand toCommand(String value) {
    try {
      PaymentApprovedEvent event =
          objectMapper.readValue(value, PaymentApprovedEvent.class);
      return new CompleteOrderPaymentCommand(
          event.orderId(), event.paymentKey(), event.amount(), event.approvedAt(), event.paymentMethod());
    } catch (JsonProcessingException e) {
      throw new InvalidPaymentEventPayloadException();
    }
  }
}
