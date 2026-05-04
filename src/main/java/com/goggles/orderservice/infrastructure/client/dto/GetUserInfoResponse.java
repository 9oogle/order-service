package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.UserInfo;
import java.util.UUID;

public record GetUserInfoResponse(UUID userId, String userName, String userEmail) {
  public UserInfo toUserInfo() {
    return new UserInfo(userId, userName, userEmail);
  }
}
