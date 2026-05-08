package com.goggles.orderservice.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.common.util.TimeUtil;
import com.goggles.orderservice.application.dto.command.FailOrderPaymentCommand;
import com.goggles.orderservice.application.service.OrderCommandService;
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
public class PaymentFailedConsumer {
  public static final String TOPIC = "payment.failed";
  public static final String GROUP_NAME = "order-service.payment-failed";

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

    orderCommandService.failOrderPayment(toCommand(record.value()));
  }

  private FailOrderPaymentCommand toCommand(String value) {
    try {
      PaymentFailedEvent event = objectMapper.readValue(value, PaymentFailedEvent.class);
      return new FailOrderPaymentCommand(
          event.orderId(), event.amount(), TimeUtil.toLocalDateTime(event.failedAt()), event.failureReason());
    } catch (JsonProcessingException e) {
      throw new InvalidPaymentEventPayloadException();
    }
  }
}
