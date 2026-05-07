package com.goggles.orderservice.infrastructure.client;

import com.goggles.common.response.ApiResponse;
import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.RollbackMentoringBookingData;
import com.goggles.orderservice.application.port.out.MentoringProvider;
import com.goggles.orderservice.infrastructure.client.dto.CancelMentoringBookingRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveMentoringRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.RollbackMentoringBookingRequest;
import com.goggles.orderservice.infrastructure.client.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    ApiResponse<ReserveProductResponse> response =
        mentoringClient.reserveEnrollment(
            data.userId(),
            data.userRole().name(),
            data.userName(),
            ReserveMentoringRequest.from(data));

    return response.data().toProductItem();
  }

  private ProductReserveInfo reserveEnrollmentFallback(
      MentoringProductReserveData data, Throwable t) {
    log.warn(
        "mentoring-service reserve fallback. type: {}, cause: {}",
        t.getClass().getName(),
        t.getMessage(),
        t);

    if (t instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    if (t instanceof java.net.SocketTimeoutException) {
      throw new ExternalServiceException("요청 처리 시간이 초과되었습니다. 잠시 후 다시 주문을 시도해주세요.");
    }
    throw new ExternalServiceException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
  }

  @Override
  @CircuitBreaker(
      name = "mentoring-service-cancel",
      fallbackMethod = "cancelMentoringBookingFallback")
  @Retry(name = "mentoring-service-cancel")
  public void cancelMentoringBooking(CancelMentoringBookingData data) {
    mentoringClient.cancelMentoringBooking(
        data.userId(),
        data.userRole().name(),
        data.bookingId(),
        CancelMentoringBookingRequest.from(data));
  }

  private void cancelMentoringBookingFallback(CancelMentoringBookingData data, Throwable t) {
    log.error(
        "[멘토링 주문 취소 최종 실패] " + "userId: {}, bookingId: {}, cause: {}",
        data.userId(),
        data.bookingId(),
        t.getMessage(),
        t);

    if (t instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    throw new ExternalServiceException("멘토링 주문 취소 처리 중 오류가 발생했습니다.");
  }

  @Override
  @CircuitBreaker(
      name = "mentoring-service-rollback",
      fallbackMethod = "rollbackMentoringBookingFallback")
  @Retry(name = "mentoring-service-rollback")
  public void rollbackMentoringBooking(RollbackMentoringBookingData data) {
    mentoringClient.rollbackMentoringBooking(
        data.userId(),
        data.bookingId(),
        new RollbackMentoringBookingRequest(data.cancelReason()));
  }

  private void rollbackMentoringBookingFallback(RollbackMentoringBookingData data, Throwable t) {
    log.error(
        "[멘토링 등록 취소 보상 트랜잭션 최종 실패] " + "userId: {}, bookingId: {}, cancelReason: {}, cause: {}",
        data.userId(),
        data.bookingId(),
        data.cancelReason(),
        t.getMessage(),
        t);

    throw new ExternalServiceException("멘토링 등록 취소 처리 중 오류가 발생했습니다.");
  }
}
