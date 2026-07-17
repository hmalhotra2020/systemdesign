package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.IdempotencyIntent;
import com.example.demo.service.IntentService;

@RestController
@RequestMapping("/api/intents")
public class IntentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntentController.class);

    private final IntentService intentService;

    public IntentController(IntentService intentService) {
        this.intentService = intentService;
    }

    @GetMapping("/{idempotencyKey}")
    public ResponseEntity<IdempotencyIntent> getIntent(@PathVariable String idempotencyKey) {
        LOGGER.info("Resolving intent {}", idempotencyKey);
        return intentService.findByKey(idempotencyKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
