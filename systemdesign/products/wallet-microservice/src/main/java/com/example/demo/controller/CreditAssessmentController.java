package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CreditAssessment;
import com.example.demo.service.CreditAssessmentService;

@RestController
@RequestMapping("/api/credit-assessments")
public class CreditAssessmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditAssessmentController.class);

    private final CreditAssessmentService creditAssessmentService;

    public CreditAssessmentController(CreditAssessmentService creditAssessmentService) {
        this.creditAssessmentService = creditAssessmentService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditAssessment>> getAssessmentsForCustomer(@PathVariable Long customerId) {
        LOGGER.info("Fetching credit assessments for customer {}", customerId);
        return ResponseEntity.ok(creditAssessmentService.getAssessmentsForCustomer(customerId));
    }

    @GetMapping("/{assessmentId}")
    public ResponseEntity<CreditAssessment> getAssessment(@PathVariable Long assessmentId) {
        LOGGER.info("Fetching credit assessment {}", assessmentId);
        return ResponseEntity.ok(creditAssessmentService.getAssessmentById(assessmentId));
    }
}
