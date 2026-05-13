package com.goggles.orderservice.application.service.impl;

import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import com.goggles.orderservice.application.dto.command.CancelOrderPaymentCommand;
import com.goggles.orderservice.application.dto.command.CompleteOrderPaymentCommand;
import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.command.FailOrderPaymentCommand;
import com.goggles.orderservice.application.dto.external.CancelLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import com.goggles.orderservice.application.dto.external.CancelPendingLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.CancelPendingMentoringBookingData;
import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.RollbackLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.RollbackMentoringBookingData;
import com.goggles.orderservice.application.dto.external.UserInfo;
import com.goggles.orderservice.application.dto.result.CancelOrderResult;
import com.goggles.orderservice.application.dto.result.CreateOrderResult;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.application.port.out.UserReader;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.domain.event.NotificationOrderCanceledEvent;
import com.goggles.orderservice.domain.event.NotificationOrderCompletedEvent;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderFailedEvent;
import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.NotFoundOrderException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
import com.goggles.orderservice.domain.model.CancelReason;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderItem;
import com.goggles.orderservice.domain.model.OrderItemSpec;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.repository.OrderRepository;
import com.goggles.orderservice.infrastructure.client.exception.ExternalServiceException;
import com.goggles.orderservice.infrastructure.slack.SlackNotifier;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCommandServiceImpl implements OrderCommandService {
  private final LectureProvider lectureProvider;
  private final MentoringProvider mentoringProvider;
  private final UserReader userReader;
  private final OrderRepository orderRepository;
  private final OrderEvents orderEvents;
  private final SlackNotifier slackNotifier;

  @Override
  @Transactional
  public CreateOrderResult createLectureOrder(CreateLectureOrderCommand command) {
    UserInfo userInfo;
    try {
      userInfo = getUserInfo(command.userId());
    } catch (ExternalServiceException e) {
      log.warn("[유저 조회 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
      throw e;
    }

    List<ProductReserveInfo> productInfo;
    try {
      productInfo = reserveLecture(command, userInfo.userName());
    } catch (ExternalServiceException e) {
      log.warn("[강의 예약 실패] userId: {}, cause: {}", userInfo.userId(), e.getMessage());
      throw e;
    }

    Long totalPrice = calculateTotalPriceFromList(productInfo);
    List<OrderItemSpec> itemSpecs = convertOrderItemSpecs(productInfo, OrderItemType.LECTURE);

    try {
      Order order =
          Order.create(
              new Orderer(userInfo.userId(), userInfo.userName(), userInfo.userEmail()),
              null,
              new OrderPrice(totalPrice, 0L),
              itemSpecs,
              orderEvents);

      order = orderRepository.createOrder(order);
      return CreateOrderResult.from(order);
    } catch (Exception e) {
      log.error(
          "[강의 생성 실패] userId: {}, lectureIds: {}, cause: {}",
          userInfo.userId(),
          productInfo.stream().map(ProductReserveInfo::productId).toList(),
          e.getMessage(),
          e);
      compensateLectureReservation(
          productInfo.stream().map(ProductReserveInfo::enrollmentId).toList(), userInfo);
      throw e;
    }
  }

  @Override
  @Transactional
  public CreateOrderResult createMentoringOrder(CreateMentoringOrderCommand command) {
    UserInfo userInfo;
    try {
      userInfo = getUserInfo(command.userId());
    } catch (ExternalServiceException e) {
      log.warn("[유저 조회 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
      throw e;
    }

    ProductReserveInfo productInfo;
    try {
      productInfo = reserveMentoring(command, userInfo.userName());
    } catch (ExternalServiceException e) {
      log.warn("[멘토링 예약 실패] userId: {}, cause: {}", userInfo.userId(), e.getMessage());
      throw e;
    }

    OrderItemSpec itemSpec = productInfo.toOrderItemSpec(OrderItemType.MENTORING);

    try {
      Order order =
          Order.create(
              new Orderer(userInfo.userId(), userInfo.userName(), userInfo.userEmail()),
              null,
              new OrderPrice(productInfo.productPrice(), 0L),
              itemSpec,
              orderEvents);

      order = orderRepository.createOrder(order);
      return CreateOrderResult.from(order);
    } catch (Exception e) {
      log.error(
          "[멘토링 주문 생성 실패] userId: {}, mentoringId: {}, cause: {}",
          userInfo.userId(),
          productInfo.productId(),
          e.getMessage(),
          e);
      compensateMentoringReservation(productInfo.enrollmentId(), userInfo);
      throw e;
    }
  }

  @Override
  @Transactional
  public CancelOrderResult cancelLectureOrder(CancelLectureOrderCommand command) {
    Order order = getOrderByIdAndUserId(command.orderId(), command.userId());

    if (order.isPendingOrPaid()) {
      try {
        lectureProvider.cancelPendingLectureEnrollment(
            CancelPendingLectureEnrollmentData.from(command));
      } catch (ExternalServiceException e) {
        log.warn("[강의 결제 전 취소 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
        throw e;
      }
    } else if (order.isCompleted()) {
      try {
        lectureProvider.cancelLectureEnrollment(CancelLectureEnrollmentData.from(command));
      } catch (ExternalServiceException e) {
        log.warn("[강의 예약 취소 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
        throw e;
      }
    } else {
      throw new InvalidOrderException(OrderErrorCode.INVALID_ORDER_STATUS);
    }

    try {
      order.cancelRequest(
          CancelReason.from(command.cancelReason()), command.cancelDescription(), orderEvents);
      return CancelOrderResult.from(order);
    } catch (Exception e) {
      log.error(
          "[강의 주문 취소 실패] userId: {}, enrollmentIds: {}, cause: {}",
          command.userId(),
          command.enrollmentIds().stream().toList(),
          e.getMessage(),
          e);
      compensateLectureReservation(
          order.getItems().stream().map(OrderItem::getEnrollmentId).toList(),
          CancelReason.SYSTEM_ERROR,
          order);
      throw e;
    }
  }

  @Override
  @Transactional
  public CancelOrderResult cancelMentoringOrder(CancelMentoringOrderCommand command) {
    Order order = getOrderByIdAndUserId(command.orderId(), command.userId());

    if (order.isPendingOrPaid()) {
      try {
        mentoringProvider.cancelPendingMentoringBooking(
            CancelPendingMentoringBookingData.from(command));
      } catch (ExternalServiceException e) {
        log.warn("[멘토링 결제 전 취소 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
        throw e;
      }
    } else if (order.isCompleted()) {
      try {
        mentoringProvider.cancelMentoringBooking(CancelMentoringBookingData.from(command));
      } catch (ExternalServiceException e) {
        log.warn("[멘토링 예약 취소 실패] userId: {}, cause: {}", command.userId(), e.getMessage());
        throw e;
      }
    } else {
      throw new InvalidOrderException(OrderErrorCode.INVALID_ORDER_STATUS);
    }

    try {
      order.cancelRequest(
          CancelReason.from(command.cancelReason()), command.cancelDescription(), orderEvents);
      return CancelOrderResult.from(order);
    } catch (Exception e) {
      log.error(
          "[멘토링 주문 취소 실패] userId: {}, bookingId: {}, cause: {}",
          command.userId(),
          command.enrollmentId(),
          e.getMessage(),
          e);
      compensateMentoringReservation(
          order.getItems().getFirst().getEnrollmentId(), CancelReason.SYSTEM_ERROR, order);
      throw e;
    }
  }

  @Override
  @Transactional
  public void completeOrderPayment(CompleteOrderPaymentCommand command) {
    Order order = getOrderById(command.orderId());
    order.validateAmount(command.amount());
    order.pay(command.paymentKey(), command.paymentMethod());

    switch (order.getOrderType()) {
      case LECTURE -> order.completeLecture(orderEvents);
      case MENTORING -> order.completeMentoring(orderEvents);
    }

    orderEvents.notificationOrderCompleted(
        NotificationOrderCompletedEvent.of(order, command.approvedAt()));
  }

  @Override
  @Transactional
  public void failOrderPayment(FailOrderPaymentCommand command) {
    Order order = getOrderById(command.orderId());
    order.validateAmount(command.amount());
    order.failPayment();

    switch (order.getOrderType()) {
      case LECTURE ->
          compensateLectureReservation(
              order.getItems().stream().map(OrderItem::getEnrollmentId).toList(),
              CancelReason.PAYMENT_FAIL,
              order);

      case MENTORING ->
          compensateMentoringReservation(
              order.getItems().getFirst().getEnrollmentId(), CancelReason.PAYMENT_FAIL, order);
    }
  }

  @Override
  @Transactional
  public void cancelOrderPayment(CancelOrderPaymentCommand command) {
    Order order = getOrderById(command.orderId());
    order.validateAmount(command.amount());
    order.cancelPayment();

    switch (order.getOrderType()) {
      case LECTURE -> order.cancelLecture(orderEvents);
      case MENTORING -> order.cancelMentoring(orderEvents);
    }

    orderEvents.notificationOrderCanceled(NotificationOrderCanceledEvent.from(order));
  }

  private UserInfo getUserInfo(UUID userId) {
    return userReader.getUserInfo(userId);
  }

  private List<ProductReserveInfo> reserveLecture(
      CreateLectureOrderCommand command, String userName) {
    return lectureProvider.reserveEnrollment(LectureProductReserveData.of(command, userName));
  }

  private ProductReserveInfo reserveMentoring(
      CreateMentoringOrderCommand command, String userName) {
    return mentoringProvider.reserveEnrollment(MentoringProductReserveData.of(command, userName));
  }

  private Long calculateTotalPriceFromList(List<ProductReserveInfo> productInfo) {
    return productInfo.stream().mapToLong(ProductReserveInfo::productPrice).sum();
  }

  private List<OrderItemSpec> convertOrderItemSpecs(
      List<ProductReserveInfo> productInfo, OrderItemType type) {
    return productInfo.stream().map(product -> product.toOrderItemSpec(type)).toList();
  }

  private Order getOrderById(UUID orderId) {
    return orderRepository.getOrderById(orderId).orElseThrow(NotFoundOrderException::new);
  }

  private Order getOrderByIdAndUserId(UUID orderId, UUID userId) {
    return orderRepository
        .getOrderByIdAndUserId(orderId, userId)
        .orElseThrow(NotFoundOrderException::new);
  }

  private void compensateLectureReservation(List<UUID> enrollmentIds, UserInfo userInfo) {
    try {
      lectureProvider.rollbackLectureEnrollment(
          new RollbackLectureEnrollmentData(
              userInfo.userId(), enrollmentIds, CancelReason.SYSTEM_ERROR.name()));
    } catch (Exception e) {
      handleCompensationFailure(
          "LECTURE",
          enrollmentIds.toString(),
          userInfo.userId().toString(),
          userInfo.userEmail(),
          new OrderFailedEvent(
              null, userInfo.userId(), userInfo.userEmail(), "강의 예약 보상 트랜잭션 실패", Instant.now()),
          e);
    }
  }

  private void compensateLectureReservation(
      List<UUID> enrollmentIds, CancelReason reason, Order order) {
    try {
      lectureProvider.rollbackLectureEnrollment(
          new RollbackLectureEnrollmentData(
              order.getOrderer().getStudentId(), enrollmentIds, reason.name()));
    } catch (Exception e) {
      handleCompensationFailure(
          "LECTURE",
          enrollmentIds.toString(),
          order.getOrderer().getStudentId().toString(),
          order.getOrderer().getStudentEmail(),
          OrderFailedEvent.of(order, "강의 예약 보상 트랜잭션 실패"),
          e);
    }
  }

  private void compensateMentoringReservation(UUID enrollmentId, UserInfo userInfo) {
    try {
      mentoringProvider.rollbackMentoringBooking(
          new RollbackMentoringBookingData(
              userInfo.userId(), enrollmentId, CancelReason.SYSTEM_ERROR.name()));
    } catch (Exception e) {
      handleCompensationFailure(
          "MENTORING",
          enrollmentId.toString(),
          userInfo.userId().toString(),
          userInfo.userEmail(),
          new OrderFailedEvent(
              null, userInfo.userId(), userInfo.userEmail(), "멘토링 예약 보상 트랜잭션 실패", Instant.now()),
          e);
    }
  }

  private void compensateMentoringReservation(UUID enrollmentId, CancelReason reason, Order order) {
    try {
      mentoringProvider.rollbackMentoringBooking(
          new RollbackMentoringBookingData(
              order.getOrderer().getStudentId(), enrollmentId, reason.name()));
    } catch (Exception e) {
      handleCompensationFailure(
          "MENTORING",
          enrollmentId.toString(),
          order.getOrderer().getStudentId().toString(),
          order.getOrderer().getStudentEmail(),
          OrderFailedEvent.of(order, "멘토링 예약 보상 트랜잭션 실패"),
          e);
    }
  }

  private void handleCompensationFailure(
      String type,
      String id,
      String userId,
      String userEmail,
      OrderFailedEvent event,
      Exception e) {
    String message =
        String.format(
            "*🚨 보상 트랜잭션 실패 알림*\n"
                + "> 타입: `%s`\n"
                + "> ID: `%s`\n"
                + "> userId: `%s`\n"
                + "> 실패 시각: `%s`\n"
                + "> 예외 메시지: `%s`",
            type, id, userId, Instant.now(), e.getMessage());

    log.error("보상 트랜잭션 실패. type: {}, id: {}", type, id, e);
    orderEvents.orderFailed(event);

    try {
      slackNotifier.sendAlert(message);
    } catch (Exception slackEx) {
      log.error("Slack 알림 전송 실패", slackEx);
    }
  }
}
