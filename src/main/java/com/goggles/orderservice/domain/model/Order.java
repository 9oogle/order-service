package com.goggles.orderservice.domain.model;

import com.goggles.common.domain.BaseAudit;
import com.goggles.orderservice.domain.event.OrderEvents;
import com.goggles.orderservice.domain.event.OrderPaymentPendingEvent;
import com.goggles.orderservice.domain.exception.DuplicateOrderItemException;
import com.goggles.orderservice.domain.exception.InvalidOrderException;
import com.goggles.orderservice.domain.exception.NotFoundOrderItemException;
import com.goggles.orderservice.domain.exception.OrderErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Order extends BaseAudit {

  @Id private UUID id;

  @Embedded private Orderer orderer;

  @Embedded private Coupon coupon = null;

  @Embedded private OrderPrice price;

  @Embedded private Payment payment = null;

  @Enumerated(EnumType.STRING)
  @Column(name = "cancel_reason", length = 20)
  private CancelReason cancelReason;

  @Column(name = "cancel_description", length = 100)
  private String cancelDescription;

  @Column(name = "canceled_at")
  private LocalDateTime canceledAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private OrderStatus status = OrderStatus.PAYMENT_PENDING;

  @Getter(AccessLevel.NONE)
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  public List<OrderItem> getItems() {
    return List.copyOf(items);
  }

  public static Order create(
      Orderer orderer, Coupon coupon, OrderPrice price, List<OrderItemSpec> itemSpecs, OrderEvents events) {
    validateItems(itemSpecs);
    Order order = new Order();
    order.id = UUID.randomUUID();
    order.orderer = orderer;
    order.coupon = coupon;
    order.price = price;

    for (OrderItemSpec spec : itemSpecs) {
      order.addItem(OrderItem.create(spec.product(), spec.instructor()));
    }
    events.orderPaymentPending(OrderPaymentPendingEvent.from(order));

    return order;
  }

  public static Order create(
      Orderer orderer, Coupon coupon, OrderPrice price, OrderItemSpec itemSpec, OrderEvents events) {
    if (itemSpec == null) {
      throw new InvalidOrderException(OrderErrorCode.EMPTY_ORDER_ITEMS);
    }
    Order order = new Order();
    order.id = UUID.randomUUID();
    order.orderer = orderer;
    order.coupon = coupon;
    order.price = price;
    order.addItem(OrderItem.create(itemSpec.product(), itemSpec.instructor()));
    events.orderPaymentPending(OrderPaymentPendingEvent.from(order));

    return order;
  }

  public void pay(String paymentKey, String paymentMethod) {
    this.payment = new Payment(paymentKey, paymentMethod);
    transitionTo(OrderStatus.PAID);
  }

  public void complete() {
    transitionTo(OrderStatus.COMPLETED);
  }

  public void failPayment() {
    transitionTo(OrderStatus.PAYMENT_FAILED);
  }

  public void cancel(CancelReason reason, String description) {
    this.cancelReason = reason;
    this.cancelDescription = description;
    this.canceledAt = LocalDateTime.now();
    transitionTo(OrderStatus.CANCELED);
  }

  public void cancelItem(UUID itemId) {
    OrderItem item =
        items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(NotFoundOrderItemException::new);

    item.cancel();

    boolean allCanceled = items.stream().allMatch(i -> i.getStatus() == OrderItemStatus.CANCELED);

    if (allCanceled) {
      transitionTo(OrderStatus.CANCELED);
    }
  }

  private void transitionTo(OrderStatus next) {
    if (!this.status.canTransitionTo(next)) {
      throw new InvalidOrderException(
          OrderErrorCode.INVALID_STATUS_TRANSITION,
          this.status.getDisplayName(),
          next.getDisplayName());
    }
    this.status = next;
  }

  private void addItem(OrderItem item) {
    boolean duplicated =
        this.items.stream()
            .anyMatch(
                i ->
                    i.getProduct().getProductId().equals(item.getProduct().getProductId())
                        && i.getProduct().getProductType() == item.getProduct().getProductType());

    if (duplicated) {
      throw new DuplicateOrderItemException();
    }

    this.items.add(item);
    item.assignOrder(this);
  }

  private static void validateItems(List<OrderItemSpec> itemSpecs) {
    if (itemSpecs == null || itemSpecs.isEmpty()) {
      throw new InvalidOrderException(OrderErrorCode.EMPTY_ORDER_ITEMS);
    }

    if (itemSpecs.stream().anyMatch(Objects::isNull)) {
      throw new InvalidOrderException(OrderErrorCode.NULL_ORDER_ITEM);
    }
  }
}
