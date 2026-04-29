package com.goggles.orderservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goggles.common.exception.NotFoundException;
import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.application.dto.result.OrderDetailResult;
import com.goggles.orderservice.application.dto.result.OrderItemSummary;
import com.goggles.orderservice.application.dto.result.OrderListResult;
import com.goggles.orderservice.application.service.impl.OrderQueryServiceImpl;
import com.goggles.orderservice.domain.model.Instructor;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderItemSpec;
import com.goggles.orderservice.domain.model.OrderItemStatus;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.OrderStatus;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.model.Product;
import com.goggles.orderservice.domain.repository.OrderPageQuery;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderQueryServiceTest {
  @InjectMocks private OrderQueryServiceImpl orderQueryService;

  @Mock private OrderRepository orderRepository;

  private UUID orderId;
  private UUID userId;
  private Order order;
  private Order order1;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    order =
        Order.create(
            new Orderer(userId, "신혜원"),
            null,
            new OrderPrice(110000L, 15000L),
            List.of(
                new OrderItemSpec(
                    new Product(UUID.randomUUID(), "자바 강의", 100000L, OrderItemType.LECTURE),
                    new Instructor(UUID.randomUUID(), "강사명"))));

    order1 =
        Order.create(
            new Orderer(userId, "신혜원"),
            null,
            new OrderPrice(900000L, 5000L),
            List.of(
                new OrderItemSpec(
                    new Product(UUID.randomUUID(), "스프링 강의", 110000L, OrderItemType.LECTURE),
                    new Instructor(UUID.randomUUID(), "강사명")),
                new OrderItemSpec(
                    new Product(UUID.randomUUID(), "GITHUB 강의", 580000L, OrderItemType.LECTURE),
                    new Instructor(UUID.randomUUID(), "강사명"))));

    ReflectionTestUtils.setField(order, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(order1, "id", UUID.randomUUID());
    orderId = order.getId();
  }

  @Nested
  @DisplayName("주문 상세 조회")
  class GetOrderDetails {
    @Test
    @DisplayName("성공: 주문 상세 조회(유저 아이디)")
    void getOrderDetails_success() {
      // given
      when(orderRepository.getOrderByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));

      // when
      OrderDetailResult result = orderQueryService.getOrderDetails(orderId, userId);

      // then
      assertThat(result).isNotNull();
      assertThat(result.orderId()).isEqualTo(orderId);
      assertThat(result.orderer().getStudentId()).isEqualTo(userId);
      assertThat(result.orderItems()).hasSize(1);

      OrderItemSummary item = result.orderItems().get(0);
      assertThat(item.product().getProductName()).isEqualTo("자바 강의");
      assertThat(item.instructor().getInstructorName()).isEqualTo("강사명");
      assertThat(item.status()).isEqualTo(OrderItemStatus.ACTIVE);

      verify(orderRepository, times(1)).getOrderByIdAndUserId(orderId, userId);
    }

    @Test
    @DisplayName("실패: 주문을 찾을 수 없음")
    void getOrderDetails_notFound() {
      // given
      when(orderRepository.getOrderByIdAndUserId(orderId, userId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> orderQueryService.getOrderDetails(orderId, userId))
          .isInstanceOf(NotFoundException.class)
          .hasMessage("주문을 찾을 수 없습니다.");

      verify(orderRepository, times(1)).getOrderByIdAndUserId(orderId, userId);
    }
  }

  @Nested
  @DisplayName("주문 목록 조회(나의 주문)")
  class getOrderPage {
    @Test
    void getOrderPage_success() {
      // given
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      OrderListQuery query = OrderListQuery.of(userId, null, null, pageRequest);

      Page<Order> orderPage = new PageImpl<>(List.of(order, order1), PageRequest.of(0, 10), 2);
      given(orderRepository.getOrderPage(domainQuery(query))).willReturn(orderPage);

      // when
      Page<OrderListResult> result = orderQueryService.getOrders(query);

      // then
      assertThat(result.getTotalElements()).isEqualTo(2);
      assertThat(result.getContent()).hasSize(2);

      OrderListResult orderResult = result.getContent().get(0);
      assertThat(orderResult.orderId()).isEqualTo(order.getId());
      assertThat(orderResult.status()).isEqualTo(OrderStatus.PAYMENT_PENDING);
      assertThat(orderResult.price().getOriginalPrice()).isEqualTo(110000L);
      assertThat(orderResult.price().getFinalPrice()).isEqualTo(95000L);
      assertThat(orderResult.orderItems()).hasSize(1);
    }

    @Test
    void getOrderPage_emptyContent() {
      // given
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      OrderListQuery query = OrderListQuery.of(userId, null, null, pageRequest);

      Page<Order> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
      given(orderRepository.getOrderPage(domainQuery(query))).willReturn(emptyPage);

      // when
      Page<OrderListResult> result = orderQueryService.getOrders(query);

      // then
      assertThat(result.getTotalElements()).isEqualTo(0);
      assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getOrderPage_sorted() {
      // given
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      OrderListQuery query = OrderListQuery.of(userId, "price,desc", null, pageRequest);

      Page<Order> orderPage = new PageImpl<>(List.of(order1, order), PageRequest.of(0, 10), 2);
      given(orderRepository.getOrderPage(domainQuery(query))).willReturn(orderPage);

      // when
      Page<OrderListResult> result = orderQueryService.getOrders(query);

      // then
      assertThat(result.getContent()).hasSize(2);
      then(orderRepository).should(times(1)).getOrderPage(domainQuery(query));

      OrderListResult orderResult = result.getContent().get(0);
      assertThat(orderResult.orderId()).isEqualTo(order1.getId());
      assertThat(orderResult.status()).isEqualTo(OrderStatus.PAYMENT_PENDING);
      assertThat(orderResult.price().getOriginalPrice()).isEqualTo(900000L);
      assertThat(orderResult.price().getFinalPrice()).isEqualTo(895000L);
      assertThat(orderResult.orderItems()).hasSize(2);
    }

    @Test
    void getOrderPage_orderStatus() {
      // given
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      OrderListQuery query = OrderListQuery.of(userId, null, "PAYMENT_PENDING", pageRequest);

      Page<Order> orderPage = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
      given(orderRepository.getOrderPage(domainQuery(query))).willReturn(orderPage);

      // when
      Page<OrderListResult> result = orderQueryService.getOrders(query);

      // then
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).status()).isEqualTo(OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void OrderListResult_from_validation() {
      // given
      CommonPageRequest pageRequest = CommonPageRequest.of(0, 10);
      OrderListQuery query = OrderListQuery.of(userId, null, null, pageRequest);

      Page<Order> orderPage = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
      given(orderRepository.getOrderPage(domainQuery(query))).willReturn(orderPage);

      // when
      Page<OrderListResult> result = orderQueryService.getOrders(query);

      // then
      OrderListResult orderResult = result.getContent().get(0);
      assertThat(orderResult.orderItems()).hasSize(1);
      assertThat(orderResult.orderItems().get(0).product().getProductName()).isEqualTo("자바 강의");
      assertThat(orderResult.orderItems().get(0).product().getProductPrice()).isEqualTo(100000L);
    }

    private OrderPageQuery domainQuery(OrderListQuery query) {
      return new OrderPageQuery(
          query.userId(),
          query.orderSort(),
          query.orderStatus(),
          query.pageRequest().getPage(),
          query.pageRequest().getSize());
    }
  }
}
