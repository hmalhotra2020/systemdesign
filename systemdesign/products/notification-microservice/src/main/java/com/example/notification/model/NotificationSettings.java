package com.example.notification.model;

import java.time.LocalDateTime;

public class NotificationSettings {
    private String userId;
    private boolean emailEnabled = true;
    private boolean smsEnabled = true;
    private boolean inAppEnabled = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public NotificationSettings() {}

    public NotificationSettings(String userId) {
        this.userId = userId;
    }

    public NotificationSettings(String userId, boolean emailEnabled, boolean smsEnabled, boolean inAppEnabled) {
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
        this.inAppEnabled = inAppEnabled;
    }

    @org.jdbi.v3.core.mapper.reflect.JdbiConstructor
    public NotificationSettings(String userId, boolean emailEnabled, boolean smsEnabled, boolean inAppEnabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
        this.inAppEnabled = inAppEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }

    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
