package com.example.tablerito;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);
    private static final int ATTEMPTS = 5;

    private final RestClient http = RestClient.create();
    private final String url;
    private final String apiKey;

    public NotificationClient(@Value("${tablerito.notifications.url}") String url,
                              @Value("${tablerito.notifications.api-key}") String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    // Make sure the notification service is up before taking requests
    @PostConstruct
    void waitUntilAvailable() throws InterruptedException {
        for (int attempt = 1; attempt <= ATTEMPTS; attempt++) {
            try {
                http.get().uri(url).exchange((request, response) -> response.getStatusCode());
                return;
            } catch (ResourceAccessException e) {
                log.warn("Notification service not available (attempt {} of {})", attempt, ATTEMPTS);
                Thread.sleep(Duration.ofSeconds(2));
            }
        }
        log.warn("Starting without the notification service");
    }

    public void send(Notification notification) {
        http.post()
            .uri(url)
            .header("Authorization", "Bearer " + apiKey)
            .body(notification)
            .retrieve()
            .toBodilessEntity();
    }
}
