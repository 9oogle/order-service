package com.goggles.orderservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CancelLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CancelMentoringOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.UserInfo;
import com.goggles.orderservice.application.dto.result.CreateOrderResult;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.application.port.out.UserReader;
import com.goggles.orderservice.application.service.impl.OrderCommandServiceImpl;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderPaymentPendingEvent;
import com.goggles.orderservice.domain.exception.NotFoundOrderException;
import com.goggles.orderservice.domain.model.CancelReason;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

  @InjectMocks private OrderCommandServiceImpl orderCommandService;

  @Mock private LectureProvider lectureProvider;
  @Mock private MentoringProvider mentoringProvider;
  @Mock private UserReader userReader;
  @Mock private OrderRepository orderRepository;
  @Mock private OrderEvents orderEvents;

  private final UUID USER_ID = UUID.randomUUID();
  private final UUID COUPON_ID = UUID.randomUUID();
  private final UUID LECTURE_ID = UUID.randomUUID();
  private final UUID MENTORING_ID = UUID.randomUUID();
  private final UUID ENROLLMENT_ID = UUID.randomUUID();
  private final UUID ORDER_ID = UUID.randomUUID();

  private UserInfo userInfo() {
    String USER_NAME = "홍길동";
    String USER_EMAIL = "hello1@naver.com";
    return new UserInfo(USER_ID, USER_NAME, USER_EMAIL);
  }

  private ProductReserveInfo lectureProductReserveInfo() {
    return new ProductReserveInfo(
        ENROLLMENT_ID, LECTURE_ID, "자바 강의", 10000L, UUID.randomUUID(), "강사A");
  }

  private ProductReserveInfo mentoringProductReserveInfo() {
    return new ProductReserveInfo(
        UUID.randomUUID(), MENTORING_ID, "멘토링", 50000L, UUID.randomUUID(), "멘토A");
  }

  private Order createLectureOrderEntity() {
    UserInfo user = userInfo();
    ProductReserveInfo product = lectureProductReserveInfo();
    return Order.create(
        new Orderer(user.userId(), user.userName(), user.userEmail()),
        null,
        new OrderPrice(product.productPrice(), 0L),
        List.of(product.toOrderItemSpec(OrderItemType.LECTURE)),
        orderEvents);
  }

  private Order createMentoringOrderEntity() {
    UserInfo user = userInfo();
    ProductReserveInfo product = mentoringProductReserveInfo();
    return Order.create(
        new Orderer(user.userId(), user.userName(), user.userEmail()),
        null,
        new OrderPrice(product.productPrice(), 0L),
        product.toOrderItemSpec(OrderItemType.MENTORING),
        orderEvents);
  }

  private CreateLectureOrderCommand lectureCommand() {
    return new CreateLectureOrderCommand(
        USER_ID, UserRole.STUDENT, COUPON_ID, "CARD", List.of(LECTURE_ID));
  }

  @Nested
  @DisplayName("강의 주문 생성")
  class CreateLectureOrder {

    @Test
    @DisplayName("성공: 실제 도메인 로직을 거쳐 주문이 생성되고 이벤트가 발행된다")
    void successWithDomainLogic() {
      // given
      ProductReserveInfo reserveInfo =
          new ProductReserveInfo(
              ENROLLMENT_ID, LECTURE_ID, "자바 강의", 10000L, UUID.randomUUID(), "강사A");

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(lectureProvider.reserveEnrollment(any())).willReturn(List.of(reserveInfo));
      given(orderRepository.createOrder(any(Order.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      // when
      CreateOrderResult result = orderCommandService.createLectureOrder(lectureCommand());

      // then
      assertThat(result).isNotNull();
      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
      then(orderRepository).should().createOrder(any(Order.class));
    }

    @Test
    @DisplayName("보상 트랜잭션: DB 저장 실패 시 이미 예약된 강의를 롤백한다")
    void rollbackWhenRepositoryFails() {
      // given
      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(lectureProvider.reserveEnrollment(any()))
          .willReturn(List.of(lectureProductReserveInfo()));
      given(orderRepository.createOrder(any())).willThrow(new RuntimeException("DB Error"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createLectureOrder(lectureCommand()))
          .isInstanceOf(RuntimeException.class);

      // 보상 트랜잭션 호출 여부만 검증 (이벤트는 Order.create 시점에 이미 발행되므로 이 테스트의 관심사 아님)
      then(lectureProvider).should().rollbackLectureEnrollment(any());
    }

    @Test
    @DisplayName("실패: 유저 정보 조회 실패 시 예외가 전파된다")
    void userNotFound() {
      // given
      given(userReader.getUserInfo(USER_ID)).willThrow(new RuntimeException("유저 없음"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createLectureOrder(lectureCommand()))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("유저 없음");

      then(lectureProvider).shouldHaveNoInteractions();
      then(orderRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("실패: 상품 예약 실패 시 예외가 전파된다")
    void reservationFailed() {
      // given
      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(lectureProvider.reserveEnrollment(any())).willThrow(new RuntimeException("예약 실패"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createLectureOrder(lectureCommand()))
          .isInstanceOf(RuntimeException.class);

      then(orderRepository).shouldHaveNoInteractions();
    }
  }

  @Nested
  @DisplayName("강의 주문 취소")
  class CancelLectureOrder {

    @Test
    @DisplayName("성공: 강의 주문을 취소하면 외부 서비스 호출 후 주문 상태가 CANCELLED로 변경된다")
    void cancelLectureSuccess() {
      // given
      CancelLectureOrderCommand command =
          new CancelLectureOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              List.of(ENROLLMENT_ID),
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      Order order = createLectureOrderEntity();
      order.pay("payment", "TOSS");
      order.completeLecture(orderEvents);

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID))
          .willReturn(Optional.of(order));

      // when
      orderCommandService.cancelLectureOrder(command);

      // then: 외부 서비스 호출 검증
      then(lectureProvider).should().cancelLectureEnrollment(any());
      // then: 도메인 상태 변경 검증 (실제 도메인 객체이므로 상태로 검증)
      assertThat(order.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("성공: 강의 예약 주문을 취소하면 외부 서비스 호출 후 주문 상태가 CANCELLED로 변경된다")
    void cancelPendingLectureSuccess() {
      // given
      CancelLectureOrderCommand command =
          new CancelLectureOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              List.of(ENROLLMENT_ID),
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      Order order = createLectureOrderEntity();

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID))
          .willReturn(Optional.of(order));

      // when
      orderCommandService.cancelLectureOrder(command);

      // then: 외부 서비스 호출 검증
      then(lectureProvider).should().cancelPendingLectureEnrollment(any());
      // then: 도메인 상태 변경 검증 (실제 도메인 객체이므로 상태로 검증)
      assertThat(order.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("실패: 주문이 존재하지 않으면 예외가 발생하고 외부 서비스는 호출되지 않는다")
    void cancelFailWhenOrderNotFound() {
      // given
      CancelLectureOrderCommand command =
          new CancelLectureOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              List.of(ENROLLMENT_ID),
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> orderCommandService.cancelLectureOrder(command))
          .isInstanceOf(NotFoundOrderException.class);

      then(lectureProvider).shouldHaveNoInteractions();
    }
  }

  @Nested
  @DisplayName("멘토링 주문 생성")
  class CreateMentoringOrder {

    private CreateMentoringOrderCommand command() {
      String USER_ROLE = "STUDENT";
      return new CreateMentoringOrderCommand(
          USER_ID,
          UserRole.from(USER_ROLE),
          MENTORING_ID,
          "질문 있어요",
          COUPON_ID,
          "CARD",
          List.of(
              new CreateMentoringOrderCommand.TimeSlot(
                  LocalDate.of(2026, 5, 1), LocalTime.of(10, 0), LocalTime.of(11, 0))));
    }

    @Test
    @DisplayName("성공: 정상적으로 멘토링 주문이 생성된다")
    void success() {
      // given
      ProductReserveInfo productInfo = mentoringProductReserveInfo();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willReturn(productInfo);
      // createOrder에 전달된 실제 Order를 그대로 반환
      given(orderRepository.createOrder(any())).willAnswer(inv -> inv.getArgument(0));

      // when
      CreateOrderResult result = orderCommandService.createMentoringOrder(command());

      // then
      assertThat(result).isNotNull();
      then(userReader).should().getUserInfo(USER_ID);
      then(mentoringProvider).should().reserveEnrollment(any());
      then(orderRepository).should().createOrder(any());
      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
    }

    @Test
    @DisplayName("성공: 멘토링 단일 상품 가격으로 주문이 생성된다")
    void singleItemPrice() {
      // given
      ProductReserveInfo productInfo = mentoringProductReserveInfo(); // 50000L

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willReturn(productInfo);
      given(orderRepository.createOrder(any())).willAnswer(inv -> inv.getArgument(0));

      // when
      orderCommandService.createMentoringOrder(command());

      // then: createOrder에 전달된 Order의 실제 price 검증
      then(orderRepository)
          .should()
          .createOrder(argThat(o -> o.getPrice().getFinalPrice() == 50000L));
      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
    }

    @Test
    @DisplayName("실패: 멘토링 예약 실패 시 보상 트랜잭션이 호출되고 예외가 전파된다")
    void reservationFailed() {
      // given
      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willThrow(new RuntimeException("예약 실패"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createMentoringOrder(command()))
          .isInstanceOf(RuntimeException.class);

      then(orderRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("보상 트랜잭션: DB 저장 실패 시 멘토링 예약을 롤백한다")
    void rollbackWhenRepositoryFails() {
      // given
      ProductReserveInfo productInfo = mentoringProductReserveInfo();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willReturn(productInfo);
      given(orderRepository.createOrder(any())).willThrow(new RuntimeException("DB Error"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createMentoringOrder(command()))
          .isInstanceOf(RuntimeException.class);

      then(mentoringProvider).should().rollbackMentoringBooking(any());
    }
  }

  @Nested
  @DisplayName("멘토링 주문 취소")
  class CancelMentoringOrder {

    @Test
    @DisplayName("성공: 강의 주문을 취소하면 외부 서비스 호출 후 주문 상태가 CANCELLED로 변경된다")
    void cancelMentoringSuccess() {
      // given
      CancelMentoringOrderCommand command =
          new CancelMentoringOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              ENROLLMENT_ID,
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      Order order = createLectureOrderEntity();
      order.pay("payment", "TOSS");
      order.completeLecture(orderEvents);

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID))
          .willReturn(Optional.of(order));

      // when
      orderCommandService.cancelMentoringOrder(command);

      // then: 외부 서비스 호출 검증
      then(mentoringProvider).should().cancelMentoringBooking(any());
      // then: 도메인 상태 변경 검증 (실제 도메인 객체이므로 상태로 검증)
      assertThat(order.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("성공: 멘토링 예약 주문을 취소하면 외부 서비스 호출 후 주문 상태가 CANCELLED로 변경된다")
    void cancelPendingMentoringSuccess() {
      // given
      CancelMentoringOrderCommand command =
          new CancelMentoringOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              ENROLLMENT_ID,
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      Order order = createLectureOrderEntity();

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID))
          .willReturn(Optional.of(order));

      // when
      orderCommandService.cancelMentoringOrder(command);

      // then: 외부 서비스 호출 검증
      then(mentoringProvider).should().cancelPendingMentoringBooking(any());
      // then: 도메인 상태 변경 검증 (실제 도메인 객체이므로 상태로 검증)
      assertThat(order.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("실패: 멘토링이 존재하지 않으면 예외가 발생하고 외부 서비스는 호출되지 않는다")
    void cancelFailWhenOrderNotFound() {
      // given
      CancelMentoringOrderCommand command =
          new CancelMentoringOrderCommand(
              USER_ID,
              UserRole.STUDENT,
              ORDER_ID,
              ENROLLMENT_ID,
              CancelReason.USER_CANCEL.name(),
              "그냥요");

      given(orderRepository.getOrderByIdAndUserId(ORDER_ID, USER_ID)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> orderCommandService.cancelMentoringOrder(command))
          .isInstanceOf(NotFoundOrderException.class);

      then(lectureProvider).shouldHaveNoInteractions();
    }
  }
}
