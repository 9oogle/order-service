package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.LectureProductReserveInfo;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureClientAdapter implements LectureProvider {
  private final LectureClient lectureClient;

  @Override
  public LectureProductReserveInfo reserveEnrollment(LectureProductReserveData data) {

    ReserveLectureResponse response =
        lectureClient.reserveEnrollment(data.userId(), ReserveLectureRequest.from(data));

    return LectureProductReserveInfo.from(response);
  }
}
