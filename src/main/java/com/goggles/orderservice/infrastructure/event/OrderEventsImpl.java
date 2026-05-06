package com.goggles.orderservice.infrastructure.event;

import com.goggles.common.event.Events;
import com.goggles.orderservice.domain.event.LectureOrderCancelEvent;
import com.goggles.orderservice.domain.event.LectureOrderCompletionEvent;
import com.goggles.orderservice.domain.event.MentoringOrderCancelEvent;
import com.goggles.orderservice.domain.event.MentoringOrderCompletionEvent;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderPaymentCancelEvent;
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
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.paymentPending(), event);
  }

  @Override
  public void lectureOrderCompleted(LectureOrderCompletionEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.lectureCompleted(), event);
  }

  @Override
  public void mentoringOrderCompleted(MentoringOrderCompletionEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.mentoringCompleted(), event);
  }

  @Override
  public void orderPaymentCancelled(OrderPaymentCancelEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.paymentCancel(), event);
  }

  @Override
  public void lectureOrderCancelled(LectureOrderCancelEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.lectureCancelled(), event);
  }

  @Override
  public void mentoringOrderCancelled(MentoringOrderCancelEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.mentoringCancelled(), event);
  }
}
