package com.example.notification.config;

import com.example.notification.model.Channel;
import com.example.notification.model.InAppNotification;
import com.example.notification.model.NotificationLog;
import com.example.notification.repository.InAppNotificationRepository;
import com.example.notification.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class FakeDataSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(FakeDataSeeder.class);

    private final NotificationLogRepository logRepository;
    private final InAppNotificationRepository inAppRepository;

    public FakeDataSeeder(NotificationLogRepository logRepository, InAppNotificationRepository inAppRepository) {
        this.logRepository = logRepository;
        this.inAppRepository = inAppRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (!logRepository.findRecentLogs(1).isEmpty()) {
            logger.info("Database already contains logs. Skipping fake data seeding.");
            return;
        }

        logger.info("Seeding 30 days of historical notification logs...");
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        // Error message pool
        List<String> errorMessages = List.of(
                "SMTP host connection timed out (port 465)",
                "SMS gateway responded with HTTP 502 Bad Gateway",
                "Invalid phone format: number must start with country code",
                "Email bounced: Recipient mailbox is full or inactive",
                "In-App push notification channel closed by client device"
        );

        int totalLogs = 0;
        int totalInApp = 0;

        // Seed over 30 days (from 30 days ago to today)
        for (int i = 30; i >= 0; i--) {
            LocalDateTime day = now.minusDays(i);
            // Between 15 and 35 notifications per day
            int dailyCount = 12 + random.nextInt(20);

            for (int j = 0; j < dailyCount; j++) {
                // Hour distributed across the day
                LocalDateTime logTime = day.withHour(random.nextInt(24))
                                           .withMinute(random.nextInt(60))
                                           .withSecond(random.nextInt(60));

                String userId = "user_" + (1 + random.nextInt(15));
                String templateKey;
                Channel channel;
                String subject = null;
                String body;

                // Pick template
                double roll = random.nextDouble();
                if (roll < 0.40) {
                    templateKey = "WALLET_CREDITED";
                    channel = randomChannel(random);
                    subject = "Account Credited";
                    body = "Your wallet has been credited with $" + (5 + random.nextInt(495)) + ".00 by peer. Ref: TXN" + (100000 + random.nextInt(900000));
                } else if (roll < 0.75) {
                    templateKey = "WALLET_DEBITED";
                    channel = randomChannel(random);
                    subject = "Account Debited";
                    body = "Your wallet was debited by $" + (1 + random.nextInt(199)) + ".00 for checkout service.";
                } else if (roll < 0.88) {
                    templateKey = "OTP_VERIFICATION";
                    // OTP is SMS or EMAIL
                    channel = random.nextBoolean() ? Channel.SMS : Channel.EMAIL;
                    subject = channel == Channel.EMAIL ? "One-Time Password Verification" : null;
                    body = "Your wallet verification OTP code is " + (100000 + random.nextInt(900000)) + ". Valid for 5 minutes.";
                } else if (roll < 0.95) {
                    templateKey = "TRANSACTION_FAILED";
                    channel = randomChannel(random);
                    subject = channel == Channel.EMAIL || channel == Channel.IN_APP ? "Transaction Failed" : null;
                    body = "Transaction of $" + (10 + random.nextInt(150)) + ".00 failed. Reason: Insufficient balance or limits exceeded.";
                } else {
                    templateKey = "WELCOME";
                    channel = randomChannel(random);
                    subject = channel == Channel.EMAIL || channel == Channel.IN_APP ? "Welcome to Web Wallet!" : null;
                    body = "Hello! Your new Web Wallet is active and verified. Enjoy seamless digital transactions.";
                }

                // Pick status
                String status;
                String errMsg = null;
                double statusRoll = random.nextDouble();
                if (statusRoll < 0.85) {
                    status = "SENT";
                } else if (statusRoll < 0.94) {
                    status = "OPTED_OUT";
                    errMsg = "User opted out of this channel in notification settings";
                } else {
                    status = "FAILED";
                    errMsg = errorMessages.get(random.nextInt(errorMessages.size()));
                }

                String email = userId + "@example.com";
                String phone = "+1555" + (1000 + random.nextInt(9000));

                // Save log
                NotificationLog log = new NotificationLog(
                        null, userId, templateKey, channel, status,
                        email, phone, subject, body, errMsg, logTime
                );
                logRepository.insertWithTimestamp(log);
                totalLogs++;

                // If in-app and status is SENT, write a corresponding in-app record
                if (channel == Channel.IN_APP && "SENT".equals(status)) {
                    InAppNotification inApp = new InAppNotification(
                            userId, subject != null ? subject : "Notification", body
                    );
                    inApp.setRead(random.nextDouble() < 0.60); // 60% chance it was read already
                    // JDBI insert will default to current timestamp for created_at, but that is fine
                    inAppRepository.insert(inApp);
                    totalInApp++;
                }
            }
        }

        // Specifically seed a clean demo user "demo_user" so it's easy to test in UI
        logger.info("Seeding demo_user with specific test inbox...");
        InAppNotification demo1 = new InAppNotification("demo_user", "Welcome!", "Welcome to your Web Wallet! We are excited to have you here.");
        inAppRepository.insert(demo1);
        InAppNotification demo2 = new InAppNotification("demo_user", "Account Credited", "Your wallet has been credited with $250.00 by John Doe. Ref: TXN837492");
        inAppRepository.insert(demo2);
        InAppNotification demo3 = new InAppNotification("demo_user", "Low Balance Alert", "Your wallet balance is below $10.00. Please top up your account soon.");
        inAppRepository.insert(demo3);

        logger.info("Fake data seeding complete. Generated {} logs, {} historical in-app notifications.", totalLogs, totalInApp);
    }

    private Channel randomChannel(Random random) {
        int index = random.nextInt(3);
        return switch (index) {
            case 0 -> Channel.EMAIL;
            case 1 -> Channel.SMS;
            default -> Channel.IN_APP;
        };
    }
}
