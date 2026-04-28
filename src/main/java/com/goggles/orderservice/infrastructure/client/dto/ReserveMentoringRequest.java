package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData.ProductItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveMentoringRequest {
  private UUID mentoringId;
  private String requestMessage;
  private List<BookingTimeSlots> timeSlots;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BookingTimeSlots {
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    static BookingTimeSlots from(ProductItem item) {
      return new BookingTimeSlots(item.date(), item.startTime(), item.endTime());
    }
  }

  public static ReserveMentoringRequest from(MentoringProductReserveData data) {
    return new ReserveMentoringRequest(
        data.productId(),
        data.requestMessage(),
        data.items().stream().map(BookingTimeSlots::from).toList());
  }
}
