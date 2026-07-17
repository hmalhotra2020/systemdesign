package com.example.notification.sender;

import com.example.notification.model.InAppNotification;
import com.example.notification.repository.InAppNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInAppSender implements InAppSender {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInAppSender.class);
    private final InAppNotificationRepository repository;

    public DatabaseInAppSender(InAppNotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void send(String userId, String title, String body) {
        logger.info("Saving In-App notification for user {}", userId);
        InAppNotification notification = new InAppNotification(userId, title, body);
        Long id = repository.insert(notification);
        logger.info("In-App notification saved with ID: {}", id);
    }
}
