package com.example.notification.sender;

public interface EmailSender {
    void send(String to, String subject, String body);
}
