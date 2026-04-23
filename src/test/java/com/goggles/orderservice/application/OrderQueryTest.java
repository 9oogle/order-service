package com.goggles.orderservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goggles.common.exception.NotFoundException;
import com.goggles.orderservice.application.dto.command.CreateOrderItemCommand;
import com.goggles.orderservice.application.dto.result.OrderDetailResult;
import com.goggles.orderservice.application.dto.result.OrderItemSummary;
import com.goggles.orderservice.application.service.impl.OrderQueryServiceImpl;
import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.enums.OrderItemStatus;
import com.goggles.orderservice.domain.enums.OrderItemType;
import com.goggles.orderservice.domain.repository.OrderRepository;
import com.goggles.orderservice.domain.vo.Instructor;
import com.goggles.orderservice.domain.vo.OrderPrice;
import com.goggles.orderservice.domain.vo.Orderer;
import com.goggles.orderservice.domain.vo.Product;
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

@Slf4j
@ExtendWith(MockitoExtension.class)
public class OrderQueryTest {
  @InjectMocks private OrderQueryServiceImpl orderQueryServiceImpl;

  @Mock private OrderRepository orderRepository;

  private UUID orderId;
  private UUID userId;
  private Order order;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    List<CreateOrderItemCommand> itemCommands =
        List.of(
            new CreateOrderItemCommand(
                new Product(UUID.randomUUID(), "자바 강의", 100000L, OrderItemType.COURSE),
                new Instructor(UUID.randomUUID(), "강사명")));

    order =
        Order.create(
            new Orderer(userId, "신혜원"),
            null,
            new OrderPrice(110000L, 15000L),
            List.of(Order.createItem(
                new Product(UUID.randomUUID(), "자바 강의", 100000L, OrderItemType.COURSE),
                new Instructor(UUID.randomUUID(), "강사명"))
            )
        );

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
      OrderDetailResult result = orderQueryServiceImpl.getOrderDetails(orderId, userId);

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
      assertThatThrownBy(() -> orderQueryServiceImpl.getOrderDetails(orderId, userId))
          .isInstanceOf(NotFoundException.class)
          .hasMessage("주문을 찾을 수 없습니다.");

      verify(orderRepository, times(1)).getOrderByIdAndUserId(orderId, userId);
    }
  }
}
