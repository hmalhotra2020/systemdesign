package com.example.notification.sender;

public interface SmsSender {
    void send(String phoneNumber, String body);
}
