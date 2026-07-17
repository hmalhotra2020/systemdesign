package com.example.notification.sender;

public interface InAppSender {
    void send(String userId, String title, String body);
}
