package com.goggles.orderservice.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

  @Override
  public Optional<UUID> getCurrentAuditor() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    String userId = request.getHeader("X-User-Id");
    return Optional.ofNullable(userId).map(UUID::fromString);
  }
}
