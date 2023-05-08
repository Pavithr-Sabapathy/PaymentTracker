package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.FlexReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.exception.ReportException;

public interface ReportsExecuteService{

	List<FlexReportDefaultOutput> executeReport(String reportName, ReportProcessingRequest reportProcessingRequest) throws ReportException;
	
}