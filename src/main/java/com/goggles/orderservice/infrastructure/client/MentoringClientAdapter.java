package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.infrastructure.client.dto.ReserveMentoringRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MentoringClientAdapter implements MentoringProvider {

  private final MentoringClient mentoringClient;

  @Override
  public ProductReserveInfo reserveEnrollment(MentoringProductReserveData data) {
    ReserveProductResponse response =
        mentoringClient.reserveEnrollment(
            data.userId(),
            data.userRole().name(),
            data.userName(),
            ReserveMentoringRequest.from(data));

    return response.toProductItem();
  }
}
