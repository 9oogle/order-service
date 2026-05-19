package com.goggles.orderservice.domain.event;

public interface OrderEvents {
  void orderPaymentPending(OrderPaymentPendingEvent event);

  void paymentCancelRequested(OrderPaymentCanceledEvent event);

  void lectureOrderCompleted(LectureOrderCompletionEvent event);

  void lectureOrderCanceled(LectureOrderCanceledEvent event);

  void mentoringOrderCompleted(MentoringOrderCompletionEvent event);

  void mentoringOrderCanceled(MentoringOrderCanceledEvent event);

  void notificationOrderCompleted(NotificationOrderCompletedEvent event);

  void notificationOrderCanceled(NotificationOrderCanceledEvent event);

  void orderFailed(OrderFailedEvent event);
}
