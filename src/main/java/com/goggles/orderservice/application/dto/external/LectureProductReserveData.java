package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import com.goggles.orderservice.application.dto.command.CreateLectureOrderCommand;
import java.util.List;
import java.util.UUID;

public record LectureProductReserveData(
    List<UUID> productIds, UUID userId, UserRole userRole, String userName) {
  public static LectureProductReserveData of(CreateLectureOrderCommand command, String userName) {
    return new LectureProductReserveData(
        command.items(), command.userId(), UserRole.from(command.userRole()), userName);
  }
}