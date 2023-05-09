package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.exception.ReportException;

public interface ReportsExecuteService{

	FlexReportExecuteResponseData executeReport(String reportName, ReportProcessingRequest reportProcessingRequest) throws ReportException;

	APIResponse populateSuccessAPIRespone(FlexReportExecuteResponseData flexList);
	
}