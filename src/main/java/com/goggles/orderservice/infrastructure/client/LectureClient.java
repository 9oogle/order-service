package com.goggles.orderservice.infrastructure.client;

import com.goggles.common.response.ApiResponse;
import com.goggles.orderservice.infrastructure.client.dto.CancelLectureEnrollmentRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.RollbackLectureEnrollmentRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "lecture-service")
public interface LectureClient {
  @PostMapping("/internal/v1/lectures-enrollment")
  ApiResponse<List<ReserveProductResponse>> reserveEnrollment(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody ReserveLectureRequest request);

  @PostMapping("/internal/v1/lectures-enrollment/rollback")
  void rollbackLecturesEnrollment(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody RollbackLectureEnrollmentRequest request);

  @PostMapping("/internal/v1/lectures-enrollment/cancellation")
  void cancelLectureEnrollment(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody CancelLectureEnrollmentRequest request);
}
