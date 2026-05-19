package com.goggles.orderservice.application.port.out;

public interface SlackProvider {
  void sendAlert(String message);
}
