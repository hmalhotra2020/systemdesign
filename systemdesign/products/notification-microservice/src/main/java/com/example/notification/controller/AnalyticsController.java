package com.example.notification.controller;

import com.example.notification.model.NotificationLog;
import com.example.notification.repository.NotificationLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final NotificationLogRepository logRepository;

    public AnalyticsController(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAnalytics(@RequestParam(defaultValue = "30") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<NotificationLog> logs = logRepository.findLogsSince(startDate);

        long totalSent = logs.stream().filter(l -> "SENT".equals(l.getStatus())).count();
        long totalFailed = logs.stream().filter(l -> "FAILED".equals(l.getStatus())).count();
        long totalOptedOut = logs.stream().filter(l -> "OPTED_OUT".equals(l.getStatus())).count();

        Map<String, Long> channelDistribution = logs.stream()
                .collect(Collectors.groupingBy(l -> l.getChannel().name(), Collectors.counting()));

        Map<String, Long> statusDistribution = logs.stream()
                .collect(Collectors.groupingBy(NotificationLog::getStatus, Collectors.counting()));

        Map<String, Long> templateDistribution = logs.stream()
                .collect(Collectors.groupingBy(NotificationLog::getTemplateKey, Collectors.counting()));

        // Group by Date ("yyyy-MM-dd") and Status
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Map<String, Long>> timelineRaw = logs.stream()
                .filter(l -> l.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getCreatedAt().format(formatter),
                        Collectors.groupingBy(NotificationLog::getStatus, Collectors.counting())
                ));

        List<Map<String, Object>> timeline = timelineRaw.entrySet().stream()
                .map(entry -> {
                    String date = entry.getKey();
                    Map<String, Long> statusMap = entry.getValue();
                    Map<String, Object> dayMap = new HashMap<>();
                    dayMap.put("date", date);
                    dayMap.put("sent", statusMap.getOrDefault("SENT", 0L));
                    dayMap.put("failed", statusMap.getOrDefault("FAILED", 0L));
                    dayMap.put("optedOut", statusMap.getOrDefault("OPTED_OUT", 0L));
                    dayMap.put("total", statusMap.values().stream().mapToLong(Long::longValue).sum());
                    return dayMap;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("date")))
                .collect(Collectors.toList());

        List<NotificationLog> recentLogs = logRepository.findRecentLogs(15);

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", (long) logs.size());
        response.put("totalSent", totalSent);
        response.put("totalFailed", totalFailed);
        response.put("totalOptedOut", totalOptedOut);
        response.put("channelDistribution", channelDistribution);
        response.put("statusDistribution", statusDistribution);
        response.put("templateDistribution", templateDistribution);
        response.put("timeline", timeline);
        response.put("recentLogs", recentLogs);

        return ResponseEntity.ok(response);
    }
}
