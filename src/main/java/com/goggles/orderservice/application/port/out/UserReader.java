package com.goggles.orderservice.application.port.out;

import com.goggles.orderservice.application.dto.external.UserInfo;
import java.util.UUID;

public interface UserReader {
  UserInfo getUserInfo(UUID userId);
}
