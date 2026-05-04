package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.CancelMentoringBookingData;
import com.goggles.orderservice.application.dto.external.MentoringProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.RollbackMentoringBookingData;

public interface MentoringProvider {
  ProductReserveInfo reserveEnrollment(MentoringProductReserveData data);

  void rollbackMentoringBooking(RollbackMentoringBookingData data);

  void cancelMentoringBooking(CancelMentoringBookingData data);
}
