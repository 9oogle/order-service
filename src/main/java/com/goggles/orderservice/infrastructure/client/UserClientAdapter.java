package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.application.dto.external.UserInfo;
import com.goggles.orderservice.application.port.out.UserReader;
import com.goggles.orderservice.infrastructure.client.dto.GetUserInfoResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserReader {

  private final UserClient userClient;

  @Override
  public UserInfo getUserInfo(UUID userId) {
    GetUserInfoResponse response = userClient.getUserInfo(userId);
    return UserInfo.from(response);
  }
}
