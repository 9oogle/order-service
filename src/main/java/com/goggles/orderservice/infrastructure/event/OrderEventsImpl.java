package com.goggles.orderservice.infrastructure.event;

import com.goggles.common.event.Events;
import com.goggles.orderservice.domain.event.LectureOrderCanceledEvent;
import com.goggles.orderservice.domain.event.LectureOrderCompletionEvent;
import com.goggles.orderservice.domain.event.MentoringOrderCanceledEvent;
import com.goggles.orderservice.domain.event.MentoringOrderCompletionEvent;
import com.goggles.orderservice.domain.event.NotificationOrderCanceledEvent;
import com.goggles.orderservice.domain.event.NotificationOrderCompletedEvent;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderPaymentCanceledEvent;
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
  public void paymentCancelRequested(OrderPaymentCanceledEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.paymentCanceled(), event);
  }

  @Override
  public void lectureOrderCanceled(LectureOrderCanceledEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.lectureCanceled(), event);
  }

  @Override
  public void mentoringOrderCanceled(MentoringOrderCanceledEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.mentoringCanceled(), event);
  }

  @Override
  public void notificationOrderCompleted(NotificationOrderCompletedEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.notificationCompleted(), event);
  }

  @Override
  public void notificationOrderCanceled(NotificationOrderCanceledEvent event) {
    events.trigger(event.orderId().toString(), DOMAIN, orderTopics.notificationCanceled(), event);
  }
}
