package com.goggles.orderservice.infrastructure.client;

import com.goggles.common.response.ApiResponse;
import com.goggles.orderservice.infrastructure.client.dto.CancelMentoringBookingRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveMentoringRequest;
import com.goggles.orderservice.infrastructure.client.dto.ReserveProductResponse;
import com.goggles.orderservice.infrastructure.client.dto.RollbackMentoringBookingRequest;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "mentoring-service")
public interface MentoringClient {
  @PostMapping("/internal/v1/mentoring-booking")
  ApiResponse<ReserveProductResponse> reserveEnrollment(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @RequestHeader("X-User-Name") String userName,
      @RequestBody ReserveMentoringRequest request);

  @PatchMapping("/internal/v1/mentoring-booking/{bookingId}/rollback")
  void rollbackMentoringBooking(
      @RequestHeader("X-User-Id") UUID userId,
      @PathVariable("bookingId") UUID bookingId,
      @RequestBody RollbackMentoringBookingRequest request);

  @PatchMapping("/internal/v1/mentoring-booking/{bookingId}/cancellation")
  void cancelMentoringBooking(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @PathVariable("bookingId") UUID bookingId,
      @RequestBody CancelMentoringBookingRequest request);
}
