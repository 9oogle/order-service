package com.goggles.orderservice.domain.entity;

import com.goggles.common.domain.BaseAudit;
import com.goggles.common.exception.BadRequestException;
import com.goggles.common.exception.ConflictException;
import com.goggles.common.exception.NotFoundException;
import com.goggles.orderservice.domain.enums.OrderItemStatus;
import com.goggles.orderservice.domain.enums.OrderStatus;
import com.goggles.orderservice.domain.vo.Coupon;
import com.goggles.orderservice.domain.vo.OrderItemSpec;
import com.goggles.orderservice.domain.vo.OrderPrice;
import com.goggles.orderservice.domain.vo.Orderer;
import com.goggles.orderservice.domain.vo.Payment;
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

  @Id @GeneratedValue @UuidGenerator private UUID id;

  @Embedded private Orderer orderer;

  @Embedded private Coupon coupon = null;

  @Embedded private OrderPrice price;

  @Embedded private Payment payment = null;

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
      Orderer orderer, Coupon coupon, OrderPrice price, List<OrderItemSpec> itemSpecs) {
    validateItems(itemSpecs);
    Order order = new Order();
    order.orderer = orderer;
    order.coupon = coupon;
    order.price = price;

    for (OrderItemSpec spec : itemSpecs) {
      order.addItem(OrderItem.create(spec.product(), spec.instructor()));
    }

    return order;
  }

  public void pay(String paymentKey, String paymentName) {
    this.payment = new Payment(paymentKey, paymentName);
    transitionTo(OrderStatus.PAID);
  }

  public void complete() {
    transitionTo(OrderStatus.COMPLETED);
  }

  public void failPayment() {
    transitionTo(OrderStatus.PAYMENT_FAILED);
  }

  public void cancel() {
    transitionTo(OrderStatus.CANCELED);
  }

  public void cancelItem(UUID itemId) {
    OrderItem item =
        items.stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("존재하지 않는 주문 상품입니다: " + itemId));

    item.cancel();

    boolean allCanceled = items.stream().allMatch(i -> i.getStatus() == OrderItemStatus.CANCELED);

    if (allCanceled) {
      transitionTo(OrderStatus.CANCELED);
    }
  }

  private void transitionTo(OrderStatus next) {
    if (!this.status.canTransitionTo(next)) {
      throw new BadRequestException(
          String.format("주문 상태를 %s에서 %s로 변경할 수 없습니다.", this.status, next));
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
      throw new ConflictException(
          "동일한 상품이 이미 주문에 존재합니다: productType="
              + item.getProduct().getProductType()
              + ", productId="
              + item.getProduct().getProductId());
    }

    this.items.add(item);
    item.assignOrder(this);
  }

  private static void validateItems(List<OrderItemSpec> itemSpecs) {
    if (itemSpecs == null || itemSpecs.isEmpty()) {
      throw new BadRequestException("주문 상품은 최소 1개 이상이어야 합니다.");
    }

    if (itemSpecs.stream().anyMatch(Objects::isNull)) {
      throw new BadRequestException("주문 상품에 null 값이 포함될 수 없습니다.");
    }
  }
}
