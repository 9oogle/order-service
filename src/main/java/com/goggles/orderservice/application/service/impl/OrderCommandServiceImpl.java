package com.goggles.orderservice.application.service.impl;

import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo.ProductItem;
import com.goggles.orderservice.application.dto.external.UserInfo;
import com.goggles.orderservice.application.dto.result.CreateOrderResult;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.application.port.out.UserReader;
import com.goggles.orderservice.application.service.OrderCommandService;
import com.goggles.orderservice.domain.model.Order;
import com.goggles.orderservice.domain.model.OrderItemSpec;
import com.goggles.orderservice.domain.model.OrderItemType;
import com.goggles.orderservice.domain.model.OrderPrice;
import com.goggles.orderservice.domain.model.Orderer;
import com.goggles.orderservice.domain.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {
  private final LectureProvider lectureProvider;
  private final MentoringProvider mentoringProvider;
  private final UserReader userReader;
  private final OrderRepository orderRepository;

  @Override
  public CreateOrderResult createLectureOrder(CreateLectureOrderCommand command) {
    UserInfo userInfo = getUserInfo(command.userId());
    ProductReserveInfo productInfo = reserveLecture(command, userInfo.userName());
    Long totalPrice = calculateTotalPrice(productInfo);
    List<OrderItemSpec> itemSpecs = convertOrderItemSpecs(productInfo, OrderItemType.LECTURE);

    Order order =
        Order.create(
            new Orderer(userInfo.userId(), userInfo.userName()),
            null,
            new OrderPrice(totalPrice, 0L),
            itemSpecs);

    order = orderRepository.createOrder(order);

    return CreateOrderResult.from(order);
  }

  @Override
  public CreateOrderResult createMentoringOrder(CreateMentoringOrderCommand command) {
    UserInfo userInfo = getUserInfo(command.userId());
    ProductReserveInfo productInfo = reserveMentoring(command, userInfo.userName());
    Long totalPrice = calculateTotalPrice(productInfo);
    List<OrderItemSpec> itemSpecs = convertOrderItemSpecs(productInfo, OrderItemType.MENTORING);

    Order order =
        Order.create(
            new Orderer(userInfo.userId(), userInfo.userName()),
            null,
            new OrderPrice(totalPrice, 0L),
            itemSpecs);

    order = orderRepository.createOrder(order);

    return CreateOrderResult.from(order);
  }

  private UserInfo getUserInfo(UUID userId) {
    return userReader.getUserInfo(userId);
  }

  private ProductReserveInfo reserveLecture(CreateLectureOrderCommand command, String userName) {
    return lectureProvider.reserveEnrollment(LectureProductReserveData.of(command, userName));
  }

  private ProductReserveInfo reserveMentoring(
      CreateMentoringOrderCommand command, String userName) {
    return mentoringProvider.reserveEnrollment(MentoringProductReserveData.of(command, userName));
  }

  private Long calculateTotalPrice(ProductReserveInfo productInfo) {
    return productInfo.products().stream().mapToLong(ProductItem::productPrice).sum();
  }

  private List<OrderItemSpec> convertOrderItemSpecs(
      ProductReserveInfo productInfo, OrderItemType type) {
    return productInfo.products().stream().map(product -> product.toOrderItemSpec(type)).toList();
  }
}
