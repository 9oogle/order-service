package com.goggles.orderservice.infrastructure.config;

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
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast)
        .map(attrs -> attrs.getRequest().getHeader("X-User-Id"))
        .filter(userId -> userId != null && !userId.isBlank())
        .map(
            userId -> {
              try {
                return UUID.fromString(userId);
              } catch (IllegalArgumentException e) {
                return null;
              }
            });
  }
}
