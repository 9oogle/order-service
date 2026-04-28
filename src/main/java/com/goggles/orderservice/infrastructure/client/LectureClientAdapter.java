package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureClientAdapter implements LectureProvider {
  private final LectureClient lectureClient;

  @Override
  public ProductReserveInfo reserveEnrollment(LectureProductReserveData data) {

    ReserveProductResponse response =
        lectureClient.reserveEnrollment(
            data.userId(), data.userRole().name(), ReserveLectureRequest.from(data));

    return ProductReserveInfo.from(response);
  }
}
