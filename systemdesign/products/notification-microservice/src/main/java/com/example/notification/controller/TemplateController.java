package com.example.notification.controller;

import com.example.notification.model.Channel;
import com.example.notification.model.NotificationTemplate;
import com.example.notification.repository.NotificationTemplateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final NotificationTemplateRepository templateRepository;

    public TemplateController(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @GetMapping
    public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateRepository.findAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<List<NotificationTemplate>> getTemplatesByKey(@PathVariable String key) {
        List<NotificationTemplate> templates = templateRepository.findByKey(key);
        if (templates.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(templates);
    }

    @PostMapping
    public ResponseEntity<NotificationTemplate> saveTemplate(@RequestBody NotificationTemplate template) {
        if (template.getTemplateKey() == null || template.getTemplateKey().isBlank()) {
            throw new IllegalArgumentException("Template key cannot be null or empty");
        }
        if (template.getChannel() == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        if (template.getBodyTemplate() == null || template.getBodyTemplate().isBlank()) {
            throw new IllegalArgumentException("Body template cannot be null or empty");
        }

        templateRepository.save(template);
        return ResponseEntity.ok(template);
    }

    @DeleteMapping("/{key}/{channel}")
    public ResponseEntity<?> deleteTemplate(@PathVariable String key, @PathVariable Channel channel) {
        int deleted = templateRepository.delete(key, channel.name());
        if (deleted > 0) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Template deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
