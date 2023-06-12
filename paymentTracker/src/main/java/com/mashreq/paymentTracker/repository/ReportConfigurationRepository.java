package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Report;
@Repository
public interface ReportConfigurationRepository extends JpaRepository<Report, Long> {

	Report findByReportName(String reportName);
}
