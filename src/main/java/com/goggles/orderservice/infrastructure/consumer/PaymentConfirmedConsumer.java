package com.goggles.orderservice.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.common.util.TimeUtil;
import com.goggles.orderservice.application.dto.command.CompleteOrderPaymentCommand;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.infrastructure.event.PaymentConfirmedEvent;
import com.goggles.orderservice.infrastructure.event.exception.InvalidPaymentEventPayloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmedConsumer {
  public static final String TOPIC = "payment.confirmed.v1";
  public static final String GROUP_NAME = "order-service.payment-confirmed";

  private final OrderCommandService orderCommandService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info(
        "[Kafka] Received {} | partition={}, offset={}",
        TOPIC,
        record.partition(),
        record.offset());

    try {
      orderCommandService.completeOrderPayment(toCommand(record.value()));
      ack.acknowledge();
    } catch (InvalidPaymentEventPayloadException e) {
      log.error(
          "페이로드 파싱 실패, 스킵 처리 topic={}, partition={}, offset={}",
          TOPIC,
          record.partition(),
          record.offset(),
          e);
      ack.acknowledge();
    } catch (Exception e) {
      log.error(
          "처리 실패, 재처리 예정 topic={}, partition={}, offset={}",
          TOPIC,
          record.partition(),
          record.offset(),
          e);
      throw new RuntimeException("payment.confirmed 처리 실패", e);
    }
  }

  private CompleteOrderPaymentCommand toCommand(String value) {
    try {
      PaymentConfirmedEvent event = objectMapper.readValue(value, PaymentConfirmedEvent.class);
      return new CompleteOrderPaymentCommand(
          event.orderId(),
          event.paymentKey(),
          event.amount(),
          TimeUtil.toLocalDateTime(event.approvedAt()),
          event.paymentMethod());
    } catch (JsonProcessingException e) {
      throw new InvalidPaymentEventPayloadException();
    }
  }
}
