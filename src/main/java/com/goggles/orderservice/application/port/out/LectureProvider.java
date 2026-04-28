package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;

public interface LectureProvider {
  ProductReserveInfo reserveEnrollment(LectureProductReserveData data);
}
