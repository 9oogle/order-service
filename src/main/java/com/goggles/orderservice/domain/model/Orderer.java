package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;
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

  @Column(name = "student_email", nullable = false, length = 30)
  private String studentEmail;

  public Orderer(UUID studentId, String studentName, String studentEmail) {
    validate(studentId, studentName, studentEmail);
    this.studentId = studentId;
    this.studentName = studentName;
    this.studentEmail = studentEmail;
  }

  public static void validate(UUID studentId, String studentName, String studentEmail) {
    if (studentId == null) {
      throw new InvalidVoException(VoErrorCode.MISSING_STUDENT_ID);
    }

    if (studentName == null || studentName.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_STUDENT_NAME);
    }

    if (studentEmail == null || studentEmail.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_STUDENT_EMAIL);
    }
  }
}
