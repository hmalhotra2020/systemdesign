package com.example.demo.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.example.demo.model.CreditAssessment;

public interface CreditAssessmentDao {

    @SqlQuery("SELECT * FROM credit_assessments WHERE customer_id = :customerId ORDER BY assessed_at DESC")
    List<CreditAssessment> findByCustomerId(@Bind("customerId") Long customerId);

    @SqlQuery("SELECT * FROM credit_assessments WHERE assessment_id = :assessmentId")
    CreditAssessment findById(@Bind("assessmentId") Long assessmentId);
}
