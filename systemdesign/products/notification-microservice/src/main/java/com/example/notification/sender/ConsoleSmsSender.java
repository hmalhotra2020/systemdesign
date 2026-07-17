package com.example.notification.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleSmsSender implements SmsSender {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleSmsSender.class);

    @Override
    public void send(String phoneNumber, String body) {
        logger.info("========================================");
        logger.info("SENDING SMS (Simulated AWS SNS)");
        logger.info("To: {}", phoneNumber);
        logger.info("Body: {}", body);
        logger.info("========================================");
    }
}
