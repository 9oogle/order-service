package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.infrastructure.client.dto.CancelLectureEnrollmentRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveLectureRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "lecture-service")
public interface LectureClient {
  @PostMapping("/internal/v1/lectures-enrollment")
  List<ReserveProductResponse> reserveEnrollment(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestBody ReserveLectureRequest request);

  @PostMapping("/internal/v1/lectures-enrollment/cancellation")
  void cancelMentoringBooking(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestBody CancelLectureEnrollmentRequest request);
}
