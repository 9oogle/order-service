package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.application.common.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MentoringProductReserveData(
    UUID userId, UserRole userRole, String userName,
    UUID productId, String requestMessage, List<ProductItem> items
) {
  public record ProductItem(
      LocalDate date,
      LocalDateTime startTime,
      LocalDateTime endTime
  ){}
}
