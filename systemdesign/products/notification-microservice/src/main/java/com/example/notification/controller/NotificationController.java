package com.example.notification.controller;

import com.example.notification.model.*;
import com.example.notification.repository.InAppNotificationRepository;
import com.example.notification.repository.NotificationSettingsRepository;
import com.example.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingsRepository settingsRepository;
    private final InAppNotificationRepository inAppNotificationRepository;

    public NotificationController(NotificationService notificationService,
                                  NotificationSettingsRepository settingsRepository,
                                  InAppNotificationRepository inAppNotificationRepository) {
        this.notificationService = notificationService;
        this.settingsRepository = settingsRepository;
        this.inAppNotificationRepository = inAppNotificationRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody SendNotificationRequest request) {
        try {
            Map<Channel, String> results = notificationService.sendNotification(request);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/in-app")
    public ResponseEntity<List<InAppNotification>> getInAppNotifications(@RequestParam String userId) {
        List<InAppNotification> list = inAppNotificationRepository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/in-app/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        int updated = inAppNotificationRepository.markAsRead(id, true);
        if (updated > 0) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Notification marked as read"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/settings/{userId}")
    public ResponseEntity<NotificationSettings> getSettings(@PathVariable String userId) {
        NotificationSettings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> new NotificationSettings(userId, true, true, true));
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/settings/{userId}")
    public ResponseEntity<NotificationSettings> updateSettings(@PathVariable String userId, @RequestBody NotificationSettings settings) {
        // Enforce the path userId matches the body userId
        settings.setUserId(userId);
        settingsRepository.save(settings);
        return ResponseEntity.ok(settings);
    }
}
