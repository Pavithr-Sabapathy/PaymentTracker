package com.mashreq.paymentTracker.dao;

import java.util.Date;

import com.mashreq.paymentTracker.model.ReportExecution;

public interface ReportExecutionDAO {

	ReportExecution save(ReportExecution reportExecution);

	void updateStatusById(Long executionId, String value, Date endDate);

	void updateReportExecutionTimeByExecutionId(Long executionTime, Long executionId);

}