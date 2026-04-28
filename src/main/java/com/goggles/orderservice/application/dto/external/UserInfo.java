package com.goggles.orderservice.application.dto.external;

import com.goggles.orderservice.infrastructure.client.dto.GetUserInfoResponse;
import java.util.UUID;

public record UserInfo(
    UUID userId,
    String userName
) {
  public static UserInfo from(GetUserInfoResponse response) {
    return new UserInfo(
        response.getUserId(),
        response.getUserName()
    );
  }
}
