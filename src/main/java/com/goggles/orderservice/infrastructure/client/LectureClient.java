package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "lecture-service")
public interface LectureClient {
  @GetMapping("/internal/v1/lectures-enrollment/reserve")
  ReserveLectureResponse reserveEnrollment(
      @RequestHeader("X-User-Id") UUID userId, @RequestBody ReserveLectureRequest request);
}
