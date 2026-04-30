package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;

public interface MentoringProvider {
  ProductReserveInfo reserveEnrollment(MentoringProductReserveData data);

  void cancelMentoringBooking(CancelMentoringBookingData data);
}
