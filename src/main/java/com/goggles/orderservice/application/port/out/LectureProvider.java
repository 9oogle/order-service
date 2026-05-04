package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.CancelLectureEnrollmentData;
import com.goggles.orderservice.application.dto.external.LectureProductReserveData;
import com.goggles.orderservice.application.dto.external.ProductReserveInfo;
import com.goggles.orderservice.application.dto.external.RollbackLectureEnrollmentData;
import java.util.List;

public interface LectureProvider {
  List<ProductReserveInfo> reserveEnrollment(LectureProductReserveData data);

  void rollbackLectureEnrollment(RollbackLectureEnrollmentData data);

  void cancelLectureEnrollment(CancelLectureEnrollmentData data);
}
