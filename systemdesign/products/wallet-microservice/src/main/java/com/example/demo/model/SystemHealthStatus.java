package com.example.demo.model;

public record SystemHealthStatus(
        long pendingCount,
        long processingCount,
        long completedCount,
        long failedCount,
        long cancelledCount,
        long reversedCount,
        long totalCount) {
}
