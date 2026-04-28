package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveLectureRequest {
  private List<UUID> lectureIds;
  private String userName;

  public static ReserveLectureRequest from(LectureProductReserveData data) {
    return new ReserveLectureRequest(data.productIds().stream().toList(), data.userName());
  }
}
