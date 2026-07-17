package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CreditAssessmentDao;
import com.example.demo.model.CreditAssessment;

@Service
public class CreditAssessmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditAssessmentService.class);

    private final CreditAssessmentDao creditAssessmentDao;

    public CreditAssessmentService(CreditAssessmentDao creditAssessmentDao) {
        this.creditAssessmentDao = creditAssessmentDao;
    }

    public List<CreditAssessment> getAssessmentsForCustomer(Long customerId) {
        LOGGER.info("Fetching assessments for customer {}", customerId);
        return creditAssessmentDao.findByCustomerId(customerId);
    }

    public CreditAssessment getAssessmentById(Long assessmentId) {
        LOGGER.info("Fetching assessment {}", assessmentId);
        return creditAssessmentDao.findById(assessmentId);
    }
}
