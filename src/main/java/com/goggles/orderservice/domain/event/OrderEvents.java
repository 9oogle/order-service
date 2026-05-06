package com.goggles.orderservice.domain.event;

public interface OrderEvents {
  void orderPaymentPending(OrderPaymentPendingEvent event);

  void lectureOrderCompleted(LectureOrderCompletionEvent event);

  void mentoringOrderCompleted(MentoringOrderCompletionEvent event);

  void orderPaymentCancelled(OrderPaymentCancelEvent event);

  void lectureOrderCancelled(LectureOrderCancelEvent event);

  void mentoringOrderCancelled(MentoringOrderCancelEvent event);
}
