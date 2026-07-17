package com.example.notification.model;

import java.time.LocalDateTime;

public class InAppNotification {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private boolean read = false; // Maps to is_read
    private LocalDateTime createdAt;

    // Constructors
    public InAppNotification() {}

    public InAppNotification(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    @org.jdbi.v3.core.mapper.reflect.JdbiConstructor
    public InAppNotification(Long id, String userId, String title, String content, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
