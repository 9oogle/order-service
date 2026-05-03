package com.goggles.orderservice.infrastructure.event;

import com.goggles.common.event.Events;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderPaymentPendingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OrderTopics.class)
public class OrderEventsImpl implements OrderEvents {

  private final OrderTopics orderTopics;
  private final Events events;

  public static final String DOMAIN = "ORDER";

  @Override
  public void orderPaymentPending(OrderPaymentPendingEvent event) {
    log.info("[OrderEvents] orderPaymentPending 호출 - orderId: {}", event.orderId());
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.paymentPending(), event);
  }
}
