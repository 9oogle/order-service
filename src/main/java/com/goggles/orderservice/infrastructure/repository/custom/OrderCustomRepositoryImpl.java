package com.goggles.orderservice.infrastructure.repository.custom;

import static com.goggles.orderservice.domain.entity.QOrder.order;

import com.goggles.orderservice.application.dto.query.OrderListQuery;
import com.goggles.orderservice.domain.entity.Order;
import com.goggles.orderservice.domain.enums.OrderSortType;
import com.goggles.orderservice.domain.enums.OrderStatus;
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
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Order> getOrderPage(OrderListQuery query) {
    List<Order> content =
        queryFactory
            .selectFrom(order)
            .where(userCondition(query.userId()), statusCondition(query.orderStatus()))
            .orderBy(orderSpecifier(query.orderSort()))
            .offset((long) query.pageRequest().getPage() * query.pageRequest().getSize())
            .limit(query.pageRequest().getSize())
            .fetch();

    Long total =
        queryFactory
            .select(order.count())
            .from(order)
            .where(userCondition(query.userId()), statusCondition(query.orderStatus()))
            .fetchOne();

    Pageable pageable =
        PageRequest.of(query.pageRequest().getPage(), query.pageRequest().getSize());

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

  private OrderSpecifier<?> orderSpecifier(OrderSortType sortType) {
    if (sortType == null) return order.createdAt.desc();
    return switch (sortType) {
      case CREATED_DESC -> order.createdAt.desc();
      case CREATED_ASC -> order.createdAt.asc();
      case PRICE_DESC -> order.price.finalPrice.desc();
      case PRICE_ASC -> order.price.finalPrice.asc();
    };
  }
}
