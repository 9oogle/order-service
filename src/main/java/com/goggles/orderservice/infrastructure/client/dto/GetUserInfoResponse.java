package com.goggles.orderservice.infrastructure.client.dto;

import com.goggles.orderservice.application.dto.external.UserInfo;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetUserInfoResponse {
  private UUID userId;
  private String userName;

  public UserInfo toUserInfo() {
    return new UserInfo(this.userId, this.userName);
  }
}
