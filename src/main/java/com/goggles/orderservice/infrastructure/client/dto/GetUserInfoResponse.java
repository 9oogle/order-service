package com.goggles.orderservice.infrastructure.client.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetUserInfoResponse {
  private UUID userId;
  private String userName;
}
