package com.goggles.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EntityScan(basePackages = {"com.goggles.orderservice", "com.goggles.common"})
@EnableJpaRepositories(basePackages = {"com.goggles.orderservice", "com.goggles.common"})
public class OrderServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }
}
