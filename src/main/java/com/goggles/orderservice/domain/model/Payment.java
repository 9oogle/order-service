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
  private String paymentMethod;

  public Payment(String paymentKey, String paymentMethod) {
    validate(paymentKey, paymentMethod);
    this.paymentKey = paymentKey;
    this.paymentMethod = paymentMethod;
  }

  private static void validate(String paymentKey, String paymentMethod) {
    if (paymentKey == null || paymentKey.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_PAYMENT_KEY);
    }
    if (paymentMethod == null || paymentMethod.isBlank()) {
      throw new InvalidVoException(VoErrorCode.MISSING_PAYMENT_NAME);
    }
  }
}
