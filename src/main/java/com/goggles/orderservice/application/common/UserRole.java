package com.goggles.orderservice.application.common;

import com.goggles.common.exception.BadRequestException;

public enum UserRole {
  STUDENT,
  INSTRUCTOR,
  MASTER;

  public static UserRole from(String value) {
    if (value == null || value.isBlank()) throw new BadRequestException("사용자 역할은 필수입니다.");
    try {
      return UserRole.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("유효하지 사용자 역할입니다: " + value);
    }
  }
}
