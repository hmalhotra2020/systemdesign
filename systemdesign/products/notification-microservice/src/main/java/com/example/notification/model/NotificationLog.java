package com.example.notification.model;

import java.time.LocalDateTime;

public class NotificationLog {
    private Long id;
    private String userId;
    private String templateKey;
    private Channel channel;
    private String status; // e.g., SENT, FAILED, OPTED_OUT
    private String recipientEmail;
    private String recipientPhone;
    private String subject;
    private String body;
    private String errorMessage;
    private LocalDateTime createdAt;

    // Constructors
    public NotificationLog() {}

    public NotificationLog(String userId, String templateKey, Channel channel, String status, 
                           String recipientEmail, String recipientPhone, String subject, String body, String errorMessage) {
        this.userId = userId;
        this.templateKey = templateKey;
        this.channel = channel;
        this.status = status;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.subject = subject;
        this.body = body;
        this.errorMessage = errorMessage;
    }

    @org.jdbi.v3.core.mapper.reflect.JdbiConstructor
    public NotificationLog(Long id, String userId, String templateKey, Channel channel, String status, 
                           String recipientEmail, String recipientPhone, String subject, String body, String errorMessage, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.templateKey = templateKey;
        this.channel = channel;
        this.status = status;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.subject = subject;
        this.body = body;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
