package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand;
import com.goggles.orderservice.application.dto.command.CreateMentoringOrderCommand.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record MentoringProductReserveData(
    UUID userId,
    UserRole userRole,
    String userName,
    UUID productId,
    String requestMessage,
    List<ProductItem> items) {
  public record ProductItem(LocalDate date, LocalTime startTime, LocalTime endTime) {
    public static ProductItem from(TimeSlot timeSlot) {
      return new ProductItem(timeSlot.date(), timeSlot.startTime(), timeSlot.endTime());
    }
  }

  public static MentoringProductReserveData of(
      CreateMentoringOrderCommand command, String userName) {
    return new MentoringProductReserveData(
        command.userId(),
        UserRole.from(command.userRole()),
        userName,
        command.mentoringId(),
        command.requestMessage(),
        command.timeSlots().stream().map(ProductItem::from).toList());
  }
}
