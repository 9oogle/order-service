package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.UserInfo;
import com.goggles.orderservice.application.port.out.UserReader;
import com.goggles.orderservice.infrastructure.client.dto.GetUserInfoResponse;
import com.goggles.orderservice.infrastructure.client.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientAdapter implements UserReader {

  private final UserClient userClient;

  @Override
  @CircuitBreaker(name = "user-service-read", fallbackMethod = "getUserInfoFallback")
  @Retry(name = "user-service-read")
  public UserInfo getUserInfo(UUID userId) {
    GetUserInfoResponse response = userClient.getUserInfo(userId);
    return response.toUserInfo();
  }

  private UserInfo getUserInfoFallback(UUID userId, Exception e) {
    log.warn("user-service circuit breaker fallback. cause: {}", e.getMessage());
    if (e instanceof CallNotPermittedException) {
      throw new ExternalServiceException("현재 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");
    }
    if (e instanceof java.net.SocketTimeoutException) {
      throw new ExternalServiceException("요청 처리 시간이 초과되었습니다. 잠시 후 다시 주문을 시도해주세요.");
    }
    throw new ExternalServiceException("주문 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
  }
}
