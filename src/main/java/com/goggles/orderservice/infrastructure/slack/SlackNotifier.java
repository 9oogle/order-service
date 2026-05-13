package com.goggles.orderservice.infrastructure.slack;

import com.goggles.orderservice.application.port.out.SlackProvider;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class SlackNotifier implements SlackProvider {

  private final WebClient slackWebClient;
  private final String channel;

  public SlackNotifier(
      @Value("${slack.token}") String token, @Value("${slack.channel}") String channel) {
    this.channel = channel;
    this.slackWebClient =
        WebClient.builder()
            .baseUrl("https://slack.com/api")
            .defaultHeader("Authorization", "Bearer " + token)
            .build();
  }

  @Override
  public void sendAlert(String message) {
    Map<String, Object> payload =
        Map.of(
            "channel", channel,
            "text", message);

    slackWebClient
        .post()
        .uri("/chat.postMessage")
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(response -> log.info("Slack 알림 전송 성공: {}", response))
        .doOnError(error -> log.error("Slack 알림 전송 실패: {}", error.getMessage()))
        .subscribe();
  }
}
