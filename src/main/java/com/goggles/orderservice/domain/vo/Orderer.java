package com.goggles.orderservice.domain.vo;

import com.goggles.common.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orderer {
  @Column(name = "student_id", nullable = false, updatable = false)
  private UUID studentId;

  @Column(name = "student_name", nullable = false, length = 20)
  private String studentName;

  public Orderer(UUID studentId, String studentName) {
    validate(studentId, studentName);
    this.studentId = studentId;
    this.studentName = studentName;
  }

  public static void validate(UUID studentId, String studentName) {
    if (studentId == null) {
      throw new BadRequestException("studentId 값은 필수입니다.");
    }

    if (studentName == null || studentName.isBlank()) {
      throw new BadRequestException("studentName 값은 필수입니다.");
    }
  }
}
