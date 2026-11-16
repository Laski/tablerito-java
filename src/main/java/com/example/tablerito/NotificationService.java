package com.example.tablerito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationClient notifications;
    private final String environment;

    public NotificationService(NotificationClient notifications, @Value("${tablerito.environment}") String environment) {
        this.notifications = notifications;
        this.environment = environment;
    }

    void notify(Notification notification) {
        if (environment.equals("production")) notifications.send(notification);
        else log.info("Simulated notification: {}", notification);
    }
}
