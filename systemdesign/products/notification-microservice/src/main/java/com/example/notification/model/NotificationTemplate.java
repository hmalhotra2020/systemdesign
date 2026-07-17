package com.example.notification.model;

import java.time.LocalDateTime;

public class NotificationTemplate {
    private String templateKey;
    private Channel channel;
    private String subjectTemplate;
    private String bodyTemplate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public NotificationTemplate() {}

    public NotificationTemplate(String templateKey, Channel channel, String subjectTemplate, String bodyTemplate) {
        this.templateKey = templateKey;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    @org.jdbi.v3.core.mapper.reflect.JdbiConstructor
    public NotificationTemplate(String templateKey, Channel channel, String subjectTemplate, String bodyTemplate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.templateKey = templateKey;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public String getSubjectTemplate() { return subjectTemplate; }
    public void setSubjectTemplate(String subjectTemplate) { this.subjectTemplate = subjectTemplate; }

    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
