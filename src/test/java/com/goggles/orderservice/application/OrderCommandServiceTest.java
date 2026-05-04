package com.goggles.orderservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.goggles.orderservice.application.common.UserRole;
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
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
  private final UUID ORDER_ID = UUID.randomUUID();
  private final String USER_ROLE = "STUDENT";
  private final String USER_NAME = "홍길동";
  private final String USER_EMAIL = "hello1@naver.com";

  private UserInfo userInfo() {
    return new UserInfo(USER_ID, USER_NAME, USER_EMAIL);
  }

  private ProductReserveInfo lectureProductReserveInfo() {
    return new ProductReserveInfo(
        UUID.randomUUID(), LECTURE_ID, "자바 강의", 10000L, UUID.randomUUID(), "강사A");
  }

  private ProductReserveInfo mentoringProductReserveInfo() {
    return new ProductReserveInfo(
        UUID.randomUUID(), MENTORING_ID, "멘토링", 50000L, UUID.randomUUID(), "멘토A");
  }

  private Order mockOrder() {
    Order order = mock(Order.class);
    given(order.getId()).willReturn(ORDER_ID);
    given(order.getOrderer()).willReturn(new Orderer(USER_ID, USER_NAME, USER_EMAIL));
    return order;
  }

  @Nested
  @DisplayName("강의 주문 생성")
  class CreateLectureOrder {

    private CreateLectureOrderCommand command() {
      return new CreateLectureOrderCommand(
          USER_ID, UserRole.from(USER_ROLE), COUPON_ID, "CARD", List.of(LECTURE_ID));
    }

    @Test
    @DisplayName("성공: 정상적으로 강의 주문이 생성된다")
    void success() {
      // given
      ProductReserveInfo productInfo = lectureProductReserveInfo();
      Order order = mockOrder();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(lectureProvider.reserveEnrollment(any())).willReturn(List.of(productInfo));
      given(orderRepository.createOrder(any())).willReturn(order);

      // when
      CreateOrderResult result = orderCommandService.createLectureOrder(command());

      // then
      assertThat(result.orderId()).isEqualTo(ORDER_ID);
      assertThat(result.studentId()).isEqualTo(USER_ID);

      then(userReader).should().getUserInfo(USER_ID);
      then(lectureProvider).should().reserveEnrollment(any());
      then(orderRepository).should().createOrder(any());

      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
    }

    @Test
    @DisplayName("성공: 여러 강의 주문 시 총 가격이 합산된다")
    void totalPriceSummed() {
      // given
      ProductReserveInfo info1 =
          new ProductReserveInfo(
              UUID.randomUUID(), UUID.randomUUID(), "강의A", 10000L, UUID.randomUUID(), "강사A");
      ProductReserveInfo info2 =
          new ProductReserveInfo(
              UUID.randomUUID(), UUID.randomUUID(), "강의B", 20000L, UUID.randomUUID(), "강사B");
      Order order = mockOrder();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(lectureProvider.reserveEnrollment(any())).willReturn(List.of(info1, info2));
      given(orderRepository.createOrder(any())).willReturn(order);

      // when
      orderCommandService.createLectureOrder(command());

      // then - 총합 30000L로 Order.create 호출됐는지 검증
      then(orderRepository)
          .should()
          .createOrder(argThat(o -> o.getPrice().getFinalPrice() == 30000L));

      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
    }

    @Test
    @DisplayName("실패: 유저 정보 조회 실패 시 예외가 전파된다")
    void userNotFound() {
      // given
      given(userReader.getUserInfo(USER_ID)).willThrow(new RuntimeException("유저 없음"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createLectureOrder(command()))
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
      assertThatThrownBy(() -> orderCommandService.createLectureOrder(command()))
          .isInstanceOf(RuntimeException.class);

      then(orderRepository).shouldHaveNoInteractions();
    }
  }

  @Nested
  @DisplayName("멘토링 주문 생성")
  class CreateMentoringOrder {

    private CreateMentoringOrderCommand command() {
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
      Order order = mockOrder();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willReturn(productInfo);
      given(orderRepository.createOrder(any())).willReturn(order);

      // when
      CreateOrderResult result = orderCommandService.createMentoringOrder(command());

      // then
      assertThat(result.orderId()).isEqualTo(ORDER_ID);
      assertThat(result.studentId()).isEqualTo(USER_ID);

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
      Order order = mockOrder();

      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willReturn(productInfo);
      given(orderRepository.createOrder(any())).willReturn(order);

      // when
      orderCommandService.createMentoringOrder(command());

      // then
      then(orderRepository)
          .should()
          .createOrder(argThat(o -> o.getPrice().getFinalPrice() == 50000L));

      then(orderEvents).should().orderPaymentPending(any(OrderPaymentPendingEvent.class));
    }

    @Test
    @DisplayName("실패: 멘토링 예약 실패 시 예외가 전파된다")
    void reservationFailed() {
      // given
      given(userReader.getUserInfo(USER_ID)).willReturn(userInfo());
      given(mentoringProvider.reserveEnrollment(any())).willThrow(new RuntimeException("예약 실패"));

      // when & then
      assertThatThrownBy(() -> orderCommandService.createMentoringOrder(command()))
          .isInstanceOf(RuntimeException.class);

      then(orderRepository).shouldHaveNoInteractions();
    }
  }
}
