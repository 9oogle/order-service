package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.LectureProductReserveInfo;

public interface LectureProvider {
  LectureProductReserveInfo reserveEnrollment(LectureProductReserveData data);
}
