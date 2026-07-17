package com.example.notification.service;

import com.example.notification.model.*;
import com.example.notification.repository.*;
import com.example.notification.sender.EmailSender;
import com.example.notification.sender.SmsSender;
import com.example.notification.sender.InAppSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationTemplateRepository templateRepository;
    private final NotificationSettingsRepository settingsRepository;
    private final NotificationLogRepository logRepository;
    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final InAppSender inAppSender;

    public NotificationService(NotificationTemplateRepository templateRepository,
                               NotificationSettingsRepository settingsRepository,
                               NotificationLogRepository logRepository,
                               EmailSender emailSender,
                               SmsSender smsSender,
                               InAppSender inAppSender) {
        this.templateRepository = templateRepository;
        this.settingsRepository = settingsRepository;
        this.logRepository = logRepository;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.inAppSender = inAppSender;
    }

    public Map<Channel, String> sendNotification(SendNotificationRequest request) {
        String userId = request.getUserId();
        String templateKey = request.getTemplateKey();

        // 1. Fetch templates for this templateKey
        List<NotificationTemplate> templates = templateRepository.findByKey(templateKey);
        if (templates.isEmpty()) {
            throw new IllegalArgumentException("No templates found for template key: " + templateKey);
        }

        // 2. Fetch user notification preferences
        NotificationSettings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("No settings found for user {}. Using defaults.", userId);
                    return new NotificationSettings(userId, true, true, true);
                });

        // 3. Determine channels to dispatch
        List<Channel> targetChannels = request.getChannels();
        Map<Channel, String> sendResults = new HashMap<>();

        for (NotificationTemplate template : templates) {
            Channel channel = template.getChannel();

            // If target channels are explicitly specified in the request, skip if not match
            if (targetChannels != null && !targetChannels.isEmpty() && !targetChannels.contains(channel)) {
                continue;
            }

            // 4. Check user preferences for this channel
            boolean isEnabled = switch (channel) {
                case EMAIL -> settings.isEmailEnabled();
                case SMS -> settings.isSmsEnabled();
                case IN_APP -> settings.isInAppEnabled();
            };

            String renderedSubject = render(template.getSubjectTemplate(), request.getParameters());
            String renderedBody = render(template.getBodyTemplate(), request.getParameters());

            if (!isEnabled) {
                logger.info("Notification skipped. User {} opted out of channel {}", userId, channel);
                logRepository.insert(new NotificationLog(
                        userId, templateKey, channel, "OPTED_OUT",
                        request.getRecipientEmail(), request.getRecipientPhone(),
                        renderedSubject, renderedBody, "User opted out of this channel"
                ));
                sendResults.put(channel, "OPTED_OUT");
                continue;
            }

            // 5. Dispatch notification based on channel
            try {
                switch (channel) {
                    case EMAIL -> {
                        String toEmail = request.getRecipientEmail();
                        if (toEmail == null || toEmail.isBlank()) {
                            throw new IllegalArgumentException("Recipient email is required for EMAIL channel");
                        }
                        emailSender.send(toEmail, renderedSubject, renderedBody);
                    }
                    case SMS -> {
                        String toPhone = request.getRecipientPhone();
                        if (toPhone == null || toPhone.isBlank()) {
                            throw new IllegalArgumentException("Recipient phone is required for SMS channel");
                        }
                        smsSender.send(toPhone, renderedBody);
                    }
                    case IN_APP -> {
                        // For IN_APP, subject is used as the title
                        String title = renderedSubject != null ? renderedSubject : "Alert";
                        inAppSender.send(userId, title, renderedBody);
                    }
                }

                // Log success
                logRepository.insert(new NotificationLog(
                        userId, templateKey, channel, "SENT",
                        request.getRecipientEmail(), request.getRecipientPhone(),
                        renderedSubject, renderedBody, null
                ));
                sendResults.put(channel, "SENT");

            } catch (Exception e) {
                logger.error("Failed to send notification to user {} via {}", userId, channel, e);
                logRepository.insert(new NotificationLog(
                        userId, templateKey, channel, "FAILED",
                        request.getRecipientEmail(), request.getRecipientPhone(),
                        renderedSubject, renderedBody, e.getMessage()
                ));
                sendResults.put(channel, "FAILED: " + e.getMessage());
            }
        }

        if (sendResults.isEmpty()) {
            throw new IllegalArgumentException("None of the requested channels matched the available template channels.");
        }

        return sendResults;
    }

    private String render(String template, Map<String, Object> params) {
        if (template == null) {
            return null;
        }
        if (params == null || params.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            String replacement = val == null ? "" : String.valueOf(val);
            result = result.replace("{{" + key + "}}", replacement);
        }
        return result;
    }
}
