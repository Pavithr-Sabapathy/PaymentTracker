package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;

public interface FlexFederatedReportService {
	public ReportExecuteResponseData processFlexReport(String reportName, ReportContext reportContext,
			ReportExecutionRequest reportExecutionRequest);
}