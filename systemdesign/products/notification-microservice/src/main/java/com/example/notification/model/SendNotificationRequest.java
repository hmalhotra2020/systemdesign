package com.example.notification.model;

import java.util.List;
import java.util.Map;

public class SendNotificationRequest {
    private String userId;
    private String templateKey;
    private List<Channel> channels;
    private Map<String, Object> parameters;
    private String recipientEmail;
    private String recipientPhone;

    // Constructors
    public SendNotificationRequest() {}

    public SendNotificationRequest(String userId, String templateKey, List<Channel> channels, 
                                   Map<String, Object> parameters, String recipientEmail, String recipientPhone) {
        this.userId = userId;
        this.templateKey = templateKey;
        this.channels = channels;
        this.parameters = parameters;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }

    public List<Channel> getChannels() { return channels; }
    public void setChannels(List<Channel> channels) { this.channels = channels; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
}
