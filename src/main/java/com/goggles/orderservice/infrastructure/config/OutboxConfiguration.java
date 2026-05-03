package com.goggles.orderservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.domain.OutboxRepository;
import com.goggles.common.event.OutboxEventListener;
import com.goggles.common.event.OutboxStatusUpdater;
import com.goggles.common.event.scheduler.OutboxRelayScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class OutboxConfiguration {

  @Bean
  public OutboxStatusUpdater outboxStatusUpdater(OutboxRepository outboxRepository,
      KafkaTemplate<String, Object> kafkaTemplate) {
    return new OutboxStatusUpdater(outboxRepository, kafkaTemplate);
  }

  @Bean
  public OutboxEventListener outboxEventListener(OutboxRepository outboxRepository,
      KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
      OutboxStatusUpdater outboxStatusUpdater) {
    return new OutboxEventListener(outboxRepository, kafkaTemplate, objectMapper,
        outboxStatusUpdater);
  }

  @Bean
  public OutboxRelayScheduler outboxRelayScheduler(OutboxRepository outboxRepository,
      KafkaTemplate<String, Object> kafkaTemplate, OutboxStatusUpdater outboxStatusUpdater) {
    return new OutboxRelayScheduler(outboxRepository, kafkaTemplate, outboxStatusUpdater);
  }
}