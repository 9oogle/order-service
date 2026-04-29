package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import java.util.List;

public interface LectureProvider {
  List<ProductReserveInfo> reserveEnrollment(LectureProductReserveData data);
}
