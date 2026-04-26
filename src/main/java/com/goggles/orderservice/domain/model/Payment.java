package com.goggles.orderservice.domain.model;

import com.goggles.orderservice.domain.exception.InvalidVoException;
import com.goggles.orderservice.domain.exception.VoErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
  @Column(name = "payment_key", length = 200)
  private String paymentKey;

  @Column(name = "payment_name", length = 100)
  private String paymentName;

  public Payment(String paymentKey, String paymentName) {
    validate(paymentKey, paymentName);
    this.paymentKey = paymentKey;
    this.paymentName = paymentName;
  }

  private static void validate(String paymentKey, String paymentName) {
    if (paymentKey == null || paymentKey.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_PAYMENT_KEY);
    }
    if (paymentName == null || paymentName.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_PAYMENT_NAME);
    }
  }
}
