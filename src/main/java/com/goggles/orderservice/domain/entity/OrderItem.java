package com.goggles.orderservice.domain.entity;

import com.goggles.common.domain.BaseAudit;
import com.goggles.common.exception.BadRequestException;
import com.goggles.orderservice.domain.enums.OrderItemStatus;
import com.goggles.orderservice.domain.vo.Instructor;
import com.goggles.orderservice.domain.vo.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseAudit {

  @Id
  @GeneratedValue
  @UuidGenerator
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, updatable = false)
  private Order order;

  @Embedded
  private Product product;

  @Embedded
  private Instructor instructor;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private OrderItemStatus status = OrderItemStatus.ACTIVE;

  public static OrderItem create(
      Product product, Instructor instructor
  ) {
    OrderItem item = new OrderItem();
    item.product = product;
    item.instructor = instructor;
    return item;
  }

  void assignOrder(Order order) {
    if (this.order != null) {
      throw new BadRequestException("이미 주문에 속한 상품입니다.");
    }
    this.order = order;
  }

  public void cancel() {
    if (this.status == OrderItemStatus.CANCELED) {
      throw new BadRequestException("이미 취소된 주문 상품입니다.");
    }
    this.status = OrderItemStatus.CANCELED;
  }
}