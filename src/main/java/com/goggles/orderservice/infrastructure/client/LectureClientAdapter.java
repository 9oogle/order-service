package com.goggles.orderservice.infrastructure.client;

import com.goggles.common.response.ApiResponse;
import com.goggles.orderservice.application.dto.external.CancelLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.CancelPendingLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.RollbackLectureEnrollmentData;
import com.goggles.orderservice.application.port.out.LectureProvider;
import com.goggles.orderservice.infrastructure.client.dto.CancelLectureEnrollmentRequest;
import com.goggles.orderservice.infrastructure.client.dto.CancelPendingLectureEnrollmentRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.RollbackLectureEnrollmentRequest;
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
public class LectureClientAdapter implements LectureProvider {
  private final LectureClient lectureClient;

  @Override
  @CircuitBreaker(name = "lecture-service-write", fallbackMethod = "reserveEnrollmentFallback")
  @Retry(name = "lecture-service-write")
  public List<ProductReserveInfo> reserveEnrollment(LectureProductReserveData data) {

    ApiResponse<List<ReserveProductResponse>> response =
        lectureClient.reserveEnrollment(
            data.userId(), data.userRole().name(), ReserveLectureRequest.from(data));

    return response.data().stream().map(ReserveProductResponse::toProductItem).toList();
  }

  private List<ProductReserveInfo> reserveEnrollmentFallback(
      LectureProductReserveData data, Throwable t) {
    log.warn(
        "lecture-service reserve fallback. type: {}, cause: {}",
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
      name = "lecture-service-cancel",
      fallbackMethod = "cancelLectureEnrollmentFallback")
  @Retry(name = "lecture-service-cancel")
  public void cancelLectureEnrollment(CancelLectureEnrollmentData data) {
    lectureClient.cancelLectureEnrollment(
        data.userId(), data.userRole().name(), CancelLectureEnrollmentRequest.from(data));
  }

  private void cancelLectureEnrollmentFallback(CancelLectureEnrollmentData data, Throwable t) {
    log.error(
        "[강의 주문 취소 최종 실패] userId: {}, enrollmentIds: {}, cause: {}",
        data.userId(),
        data.enrollmentIds(),
        t.getMessage(),
        t);

    if (t instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    throw new ExternalServiceException("강의 주문 취소 처리 중 오류가 발생했습니다.");
  }

  @Override
  @CircuitBreaker(
      name = "lecture-service-cancel",
      fallbackMethod = "cancelPendingLectureEnrollmentFallback")
  @Retry(name = "lecture-service-cancel")
  public void cancelPendingLectureEnrollment(CancelPendingLectureEnrollmentData data) {
    lectureClient.cancelPendingLectureEnrollment(
        data.userId(), data.userRole().name(), CancelPendingLectureEnrollmentRequest.from(data)
    );
  }

  private void cancelPendingLectureEnrollmentFallback(CancelPendingLectureEnrollmentData data, Throwable t) {
    log.error(
        "[강의 주문 취소 최종 실패] userId: {}, enrollmentIds: {}, cause: {}",
        data.userId(),
        data.enrollmentIds(),
        t.getMessage(),
        t);

    if (t instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    throw new ExternalServiceException("강의 주문 취소 처리 중 오류가 발생했습니다.");
  }

  @Override
  @CircuitBreaker(
      name = "lecture-service-rollback",
      fallbackMethod = "rollbackLectureEnrollmentFallback")
  @Retry(name = "lecture-service-rollback")
  public void rollbackLectureEnrollment(RollbackLectureEnrollmentData data) {
    lectureClient.rollbackLectureEnrollment(
        data.userId(), RollbackLectureEnrollmentRequest.from(data));
  }

  private void rollbackLectureEnrollmentFallback(RollbackLectureEnrollmentData data, Throwable t) {
    log.error(
        "[강의 등록 취소 보상 트랜잭션 최종 실패] " + "userId: {}, enrollmentId: {}, cancelReason: {}, cause: {}",
        data.userId(),
        data.enrollmentIds(),
        data.cancelReason(),
        t.getMessage(),
        t);

    throw new ExternalServiceException("강의 등록 취소 처리 중 오류가 발생했습니다.");
  }

}
