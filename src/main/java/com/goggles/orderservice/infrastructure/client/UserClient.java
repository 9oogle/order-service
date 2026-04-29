package com.goggles.orderservice.infrastructure.client;

import com.goggles.orderservice.infrastructure.client.dto.GetUserInfoResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
  @GetMapping("/internal/v1/user/{userId}")
  GetUserInfoResponse getUserInfo(@PathVariable("userId") UUID userId);
}
