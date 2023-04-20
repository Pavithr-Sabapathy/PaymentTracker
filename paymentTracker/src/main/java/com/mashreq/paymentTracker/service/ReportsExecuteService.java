package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.exception.ReportException;

public interface ReportsExecuteService{

	void executeReport(String reportName, ReportProcessingRequest reportProcessingRequest) throws ReportException;
	
}