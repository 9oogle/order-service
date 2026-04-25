package com.goggles.orderservice.infrastructure.repository;

import static com.goggles.orderservice.domain.entity.QOrder.order;

import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.enums.OrderSortType;
import com.goggles.orderservice.domain.enums.OrderStatus;
import com.goggles.orderservice.domain.repository.OrderPageQuery;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {

  private final JPAQueryFactory queryFactory;

  public Page<Order> getOrderPage(OrderPageQuery query) {
    List<Order> content =
        queryFactory
            .selectFrom(order)
            .where(userCondition(query.userId()), statusCondition(query.orderStatus()))
            .orderBy(orderSpecifiers(query.orderSort()))
            .orderBy(order.id.desc())
            .offset((long) query.page() * query.size())
            .limit(query.size())
            .fetch();

    Long total =
        queryFactory
            .select(order.count())
            .from(order)
            .where(userCondition(query.userId()), statusCondition(query.orderStatus()))
            .fetchOne();

    Pageable pageable = PageRequest.of(query.page(), query.size());

    return new PageImpl<>(content, pageable, total != null ? total : 0);
  }

  private BooleanExpression userCondition(UUID userId) {
    if (userId == null) return null;
    return order.orderer.studentId.eq(userId);
  }

  private BooleanExpression statusCondition(OrderStatus status) {
    if (status == null) return null;
    return order.status.eq(status);
  }

  private OrderSpecifier<?>[] orderSpecifiers(OrderSortType sortType) {
    OrderSortType resolved = sortType == null ? OrderSortType.CREATED_DESC : sortType;
    return switch (resolved) {
      case CREATED_DESC -> new OrderSpecifier<?>[] {order.createdAt.desc(), order.id.desc()};
      case CREATED_ASC -> new OrderSpecifier<?>[] {order.createdAt.asc(), order.id.asc()};
      case PRICE_DESC -> new OrderSpecifier<?>[] {order.price.finalPrice.desc(), order.id.desc()};
      case PRICE_ASC -> new OrderSpecifier<?>[] {order.price.finalPrice.asc(), order.id.asc()};
    };
  }
}
