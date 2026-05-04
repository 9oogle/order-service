package com.goggles.orderservice.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Resilience4JConfig {

  private final TimeLimiterRegistry timeLimiterRegistry;
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer(
      CircuitBreakerRegistry circuitBreakerRegistry,
      TimeLimiterRegistry timeLimiterRegistry) {

    return factory -> {
      factory.configure(
          builder -> builder
              .timeLimiterConfig(timeLimiterRegistry.getConfiguration("default-write")
                  .orElse(timeLimiterRegistry.getDefaultConfig()))
              .circuitBreakerConfig(circuitBreakerRegistry.getConfiguration("default-write")
                  .orElse(circuitBreakerRegistry.getDefaultConfig()))
              .build(),
          "lecture-service-write",
          "mentoring-service-write",
          "lecture-service-cancel",
          "mentoring-service-cancel");

      factory.configure(
          builder -> builder
              .timeLimiterConfig(timeLimiterRegistry.getConfiguration("default-rollback")
                  .orElse(timeLimiterRegistry.getDefaultConfig()))
              .circuitBreakerConfig(circuitBreakerRegistry.getConfiguration("default-rollback")
                  .orElse(circuitBreakerRegistry.getDefaultConfig()))
              .build(),
          "lecture-service-rollback",
          "mentoring-service-rollback");

      factory.configure(
          builder -> builder
              .timeLimiterConfig(timeLimiterRegistry.getConfiguration("default-read")
                  .orElse(timeLimiterRegistry.getDefaultConfig()))
              .circuitBreakerConfig(circuitBreakerRegistry.getConfiguration("default-read")
                  .orElse(circuitBreakerRegistry.getDefaultConfig()))
              .build(),
          "user-service-read");

      factory.configureDefault(id -> {
        String configName = id.contains("write") || id.contains("cancel")
            ? "default-write"
            : id.contains("rollback") ? "default-rollback" : "default-read";

        return new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(timeLimiterRegistry.getConfiguration(configName)
                .orElse(timeLimiterRegistry.getDefaultConfig()))
            .circuitBreakerConfig(circuitBreakerRegistry.getConfiguration(configName)
                .orElse(circuitBreakerRegistry.getDefaultConfig()))
            .build();
      });
    };
  }
}
