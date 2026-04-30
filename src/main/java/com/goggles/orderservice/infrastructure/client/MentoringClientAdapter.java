package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.infrastructure.client.dto.ReserveMentoringRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentoringClientAdapter implements MentoringProvider {

  private final MentoringClient mentoringClient;

  @Override
  @CircuitBreaker(name = "mentoring-service-write", fallbackMethod = "reserveEnrollmentFallback")
  @Retry(name = "mentoring-service-write")
  public ProductReserveInfo reserveEnrollment(MentoringProductReserveData data) {
    ReserveProductResponse response =
        mentoringClient.reserveEnrollment(
            data.userId(),
            data.userRole().name(),
            data.userName(),
            ReserveMentoringRequest.from(data));

    return response.toProductItem();
  }

  private List<ProductReserveInfo> reserveEnrollmentFallback(MentoringProductReserveData data, Exception e) {
    log.warn("mentoring-service circuit breaker fallback. cause: {}", e.getMessage());

    if (e instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    if (e instanceof java.net.SocketTimeoutException) {
      throw new ExternalServiceException("요청 처리 시간이 초과되었습니다. 잠시 후 다시 주문을 시도해주세요.");
    }
    throw new ExternalServiceException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
  }
}
