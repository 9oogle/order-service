package com.goggles.orderservice.domain.model;

import com.goggles.common.domain.BaseAudit;
import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
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
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class OrderItem extends BaseAudit {

  @Id @GeneratedValue @UuidGenerator private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false, updatable = false)
  private Order order;

  @Embedded private Product product;

  @Embedded private Instructor instructor;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private OrderItemStatus status = OrderItemStatus.ACTIVE;

  static OrderItem create(Product product, Instructor instructor) {
    OrderItem item = new OrderItem();
    item.product = product;
    item.instructor = instructor;
    return item;
  }

  void assignOrder(Order order) {
    if (this.order != null) {
      throw new InvalidOrderException(OrderErrorCode.ALREADY_ASSIGNED_ORDER);
    }
    this.order = order;
  }

  void cancel() {
    if (this.status == OrderItemStatus.CANCELED) {
      throw new InvalidOrderException(OrderErrorCode.ALREADY_CANCELED_ORDER_ITEM);
    }
    this.status = OrderItemStatus.CANCELED;
  }
}
