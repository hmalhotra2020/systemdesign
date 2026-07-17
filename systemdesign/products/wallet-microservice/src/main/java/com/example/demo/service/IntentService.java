package com.example.demo.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.IntentDao;
import com.example.demo.model.IdempotencyIntent;

@Service
public class IntentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntentService.class);

    private final IntentDao intentDao;

    public IntentService(IntentDao intentDao) {
        this.intentDao = intentDao;
    }

    public Optional<IdempotencyIntent> findByKey(String idempotencyKey) {
        LOGGER.info("Resolving intent by key {}", idempotencyKey);
        return intentDao.findByKey(idempotencyKey);
    }
}
