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
public class Instructor {
  @Column(name = "instructor_id", nullable = false, updatable = false)
  private UUID instructorId;

  @Column(name = "instructor_name", nullable = false, length = 100)
  private String instructorName;

  public Instructor(UUID instructorId, String instructorName) {
    validate(instructorId, instructorName);
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }

  private static void validate(UUID instructorId, String instructorName) {
    if (instructorId == null) {
      throw new InvalidVoException(VoErrorCode.MISSING_INSTRUCTOR_ID);
    }
    if (instructorName == null || instructorName.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_INSTRUCTOR_NAME);
    }
  }
}
