package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Reports;
@Repository
public interface ReportConfigurationRepository extends JpaRepository<Reports, Long> {

	Reports findByReportName(String reportName);
}
