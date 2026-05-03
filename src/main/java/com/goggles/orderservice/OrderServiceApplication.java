package com.goggles.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class OrderServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }
}
